package com.peribook.auth.interfaces;

import com.peribook.auth.application.AutenticacionFallidaException;
import com.peribook.auth.application.LoginUseCase;
import com.peribook.auth.interfaces.dto.AuthControllerMapper;
import com.peribook.auth.interfaces.dto.LoginRequest;
import com.peribook.auth.interfaces.dto.LoginResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * Controller REST que expone los endpoints de autenticación.
 * 

 * Pertenece a la capa de interfaces (el adapter primario en términos de
 * Hexagonal Architecture o la "capa de presentación" en Clean Architecture).
 * Su única responsabilidad es traducir requests HTTP a llamadas al caso de
 * uso correspondiente y devolver respuestas HTTP adecuadas.
 * 
 * 

 * Notar que el controller no contiene lógica de negocio. No valida contraseñas,
 * no genera tokens, no busca usuarios — todo eso se delega en el caso de uso
 * LoginUseCase. Esto mantiene la capa de aplicación testeable sin
 * necesidad de levantar un servidor HTTP.
 * 
 * 

 * Para el manejo de errores uso ProblemDetail (RFC 9457), el estándar
 * de Spring Framework 6+ para errores HTTP. Prefiero esto a devolver un Map
 * o una clase personalizada porque es un estándar reconocido por herramientas
 * como Postman, OpenAPI y clientes HTTP en general.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final LoginUseCase loginUseCase;
    private final AuthControllerMapper mapper;

    /**
     * Inyección por constructor. El mapper de MapStruct se inyecta como bean
     * gracias a .
     */
    public AuthController(LoginUseCase loginUseCase, AuthControllerMapper mapper) {
        this.loginUseCase = loginUseCase;
        this.mapper = mapper;
    }

    /**
     * Endpoint público para iniciar sesión.
     * 

     * El parámetro  está anotado con , así que
     * Spring Validation aplica las restricciones de LoginRequest
     * antes de que el cuerpo del método se ejecute. Si la validación falla,
     * Spring devuelve automáticamente un 400 Bad Request sin llegar al caso
     * de uso.
     * 
     *
     * @param request DTO con email y contraseña (validado por Jakarta Validation)
     * @return 200 OK con el JWT y datos del usuario, o 401 si las credenciales son inválidas
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Intento de login para: {}", request.email());
        LoginUseCase.LoginResult result = loginUseCase.login(request.email(), request.password());
        log.info("Login exitoso para: {}", request.email());
        // Uso el mapper de MapStruct para convertir el resultado del caso de uso
        // al DTO de respuesta. Esto mantiene el controller desacoplado de los
        // detalles internos de LoginResult.
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    /**
     * Manejador de errores para credenciales inválidas.
     * 

     * Captura AutenticacionFallidaException lanzada por el caso de uso
     * y devuelve un 401 Unauthorized con cuerpo ProblemDetail (RFC 9457).
     * No incluyo el stack trace ni detalles internos en la respuesta por seguridad.
     * 
     *
     * @param ex la excepción lanzada por el caso de uso
     * @return respuesta HTTP 401 con detalles del error
     */
    @ExceptionHandler(AutenticacionFallidaException.class)
    public ResponseEntity<ProblemDetail> handleAutenticacionFallida(AutenticacionFallidaException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Autenticación fallida");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidacion(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, errores);
        problem.setTitle("Error de validación");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleArgumentoInvalido(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Datos inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
