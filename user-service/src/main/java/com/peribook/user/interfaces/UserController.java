package com.peribook.user.interfaces;

import com.peribook.user.application.ObtenerPerfilUseCase;
import com.peribook.user.application.PerfilNoEncontradoException;
import com.peribook.user.domain.Perfil;
import com.peribook.user.interfaces.dto.PerfilControllerMapper;
import com.peribook.user.interfaces.dto.PerfilResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST que expone los endpoints públicos del user-service.
 * <p>
 * Sigue el patrón "capa de interfaces" de la arquitectura hexagonal:
 * recibe requests HTTP, delega en el caso de uso ({@link ObtenerPerfilUseCase}),
 * y mapea el resultado a DTOs. No contiene lógica de negocio — eso es
 * responsabilidad del dominio.
 * </p>
 * <p>
 * El {@code @RequestMapping("/api/users")} establece la raíz de todos los
 * endpoints. El BFF (bff-web) es quien realmente recibe el tráfico del
 * frontend y redirige aquí; el user-service no está expuesto directamente
 * a internet.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final ObtenerPerfilUseCase obtenerPerfilUseCase;
    private final PerfilControllerMapper mapper;

    /**
     * Inyección por constructor de las dependencias: el caso de uso y el mapper.
     * El controlador no sabe ni le importa cómo se implementan — solo las recibe
     * y las usa.
     */
    public UserController(ObtenerPerfilUseCase obtenerPerfilUseCase, PerfilControllerMapper mapper) {
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
        this.mapper = mapper;
    }

    /**
     * Obtiene el perfil público de un usuario por su UUID.
     * <p>
     * Endpoint: {@code GET /api/users/{id}}
     * </p>
     * <p>
     * El flujo es simple pero revela la arquitectura:
     * <ol>
     *   <li>El controlador recibe el request HTTP</li>
     *   <li>Delega en el caso de uso (capa de aplicación)</li>
     *   <li>El caso de uso usa el repositorio (puerto de dominio) para buscar</li>
     *   <li>El repositorio (infraestructura) consulta la base de datos</li>
     *   <li>El resultado se mapea a DTO y se devuelve como JSON</li>
     * </ol>
     * Cada capa tiene una responsabilidad y solo esa.
     * </p>
     *
     * @param id UUID del perfil (extraído de la URL)
     * @return 200 OK con el perfil, o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponse> obtenerPerfil(@PathVariable UUID id) {
        log.debug("Consultando perfil: {}", id);
        Perfil perfil = obtenerPerfilUseCase.obtener(id);
        return ResponseEntity.ok(mapper.toResponse(perfil));
    }

    /**
     * Maneja la excepción {@link PerfilNoEncontradoException} y la traduce a
     * una respuesta HTTP 404 con formato Problem Details (RFC 9457).
     * <p>
     * Decidí usar {@link ProblemDetail} de Spring MVC 6 en lugar de crear una
     * clase de error personalizada porque el estándar RFC 9457 ya cubre lo que
     * necesitamos: title, status, detail y campos adicionales si hicieran falta.
     * Además, el BFF puede propagar estos detalles al frontend de forma consistente
     * con otros microservicios.
     * </p>
     *
     * @param ex Excepción lanzada por el caso de uso
     * @return ResponseEntity con status 404 y body en formato Problem Details JSON
     */
    @ExceptionHandler(PerfilNoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleNoEncontrado(PerfilNoEncontradoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Perfil no encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }
}
