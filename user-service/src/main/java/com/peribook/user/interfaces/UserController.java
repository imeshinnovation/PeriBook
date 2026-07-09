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

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final ObtenerPerfilUseCase obtenerPerfilUseCase;
    private final PerfilControllerMapper mapper;

    public UserController(ObtenerPerfilUseCase obtenerPerfilUseCase, PerfilControllerMapper mapper) {
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponse> obtenerPerfil(@PathVariable UUID id) {
        log.debug("Consultando perfil: {}", id);
        Perfil perfil = obtenerPerfilUseCase.obtener(id);
        return ResponseEntity.ok(mapper.toResponse(perfil));
    }

    @ExceptionHandler(PerfilNoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleNoEncontrado(PerfilNoEncontradoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Perfil no encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }
}
