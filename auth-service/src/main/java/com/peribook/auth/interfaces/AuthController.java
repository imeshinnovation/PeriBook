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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final LoginUseCase loginUseCase;
    private final AuthControllerMapper mapper;

    public AuthController(LoginUseCase loginUseCase, AuthControllerMapper mapper) {
        this.loginUseCase = loginUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Intento de login para: {}", request.email());
        LoginUseCase.LoginResult result = loginUseCase.login(request.email(), request.password());
        log.info("Login exitoso para: {}", request.email());
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @ExceptionHandler(AutenticacionFallidaException.class)
    public ResponseEntity<ProblemDetail> handleAutenticacionFallida(AutenticacionFallidaException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Autenticación fallida");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
