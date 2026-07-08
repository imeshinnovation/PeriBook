package com.peribook.post.interfaces;

import com.peribook.post.application.CrearPublicacionUseCase;
import com.peribook.post.application.ListarPublicacionesUseCase;
import com.peribook.post.domain.Publicacion;
import com.peribook.post.interfaces.dto.CrearPublicacionRequest;
import com.peribook.post.interfaces.dto.PostControllerMapper;
import com.peribook.post.interfaces.dto.PublicacionResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    private final CrearPublicacionUseCase crearUseCase;
    private final ListarPublicacionesUseCase listarUseCase;
    private final PostControllerMapper mapper;

    public PostController(CrearPublicacionUseCase crearUseCase,
                          ListarPublicacionesUseCase listarUseCase,
                          PostControllerMapper mapper) {
        this.crearUseCase = crearUseCase;
        this.listarUseCase = listarUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<PublicacionResponse> crear(
            @Valid @RequestBody CrearPublicacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID autorId = UUID.fromString(jwt.getClaimAsString("userId"));
        log.info("Creando publicación. Autor: {}", autorId);
        Publicacion publicacion = crearUseCase.ejecutar(autorId, request.contenido());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(publicacion));
    }

    @GetMapping
    public ResponseEntity<List<PublicacionResponse>> listar(
            @RequestParam(defaultValue = "20") int limite) {
        List<Publicacion> publicaciones = listarUseCase.ejecutar(limite);
        List<PublicacionResponse> response = publicaciones.stream()
                .map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }
}
