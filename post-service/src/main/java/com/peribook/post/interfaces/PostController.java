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

/**
 * Controlador REST que expone los endpoints de publicaciones.
 * 

 * Esta es la capa de interfaces (inbound adapters en Hexagonal Architecture).
 * Su unica responsabilidad es traducir peticiones HTTP en llamadas a casos de uso
 * de la capa de aplicacion, y traducir las respuestas del dominio a DTOs JSON.
 * No contiene logica de negocio — toda la logica importante esta en los use cases
 * y en el dominio.
 * 

 * El endpoint  recibe el contenido del post del cuerpo de
 * la peticion y extrae el ID del autor del token JWT. Esto asegura que el autor
 * siempre es quien hizo la peticion autenticada, no un valor que el cliente pueda
 * manipular.
 * 

 * El endpoint  lista publicaciones recientes con un limite
 * configurable via query parameter (default 20).
 *
 * @author Alexander Rubio Caceres
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    private final CrearPublicacionUseCase crearUseCase;
    private final ListarPublicacionesUseCase listarUseCase;
    private final PostControllerMapper mapper;

    /**
     * Constructor con inyeccion de dependencias.
     * Recibe los dos casos de uso y el mapper de MapStruct. El mapper se inyecta
     * como interfaz, pero en tiempo de ejecucion Spring inyecta la implementacion
     * generada por MapStruct en compilacion.
     */
    public PostController(CrearPublicacionUseCase crearUseCase,
                          ListarPublicacionesUseCase listarUseCase,
                          PostControllerMapper mapper) {
        this.crearUseCase = crearUseCase;
        this.listarUseCase = listarUseCase;
        this.mapper = mapper;
    }

    /**
     * Crea una nueva publicacion.
     * 

     * El autor se obtiene del token JWT autenticado (claim "userId"). El contenido
     * viene del cuerpo de la peticion, validado por Jakarta Validation.
     * Devuelve HTTP 201 Created con la publicacion creada.
     *
     * @param request DTO con el contenido de la publicacion (validado)
     * @param jwt     token JWT autenticado (inyectado por Spring Security)
     * @return la publicacion creada con HTTP 201
     */
    @PostMapping
    public ResponseEntity<PublicacionResponse> crear(
            @Valid @RequestBody CrearPublicacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID autorId = UUID.fromString(jwt.getClaimAsString("userId"));
        log.info("Creando publicacion. Autor: {}", autorId);
        Publicacion publicacion = crearUseCase.ejecutar(autorId, request.contenido());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(publicacion));
    }

    /**
     * Lista las publicaciones mas recientes.
     * 

     * El parametro  tiene un valor por defecto de 20 y se acota a 50
     * en el caso de uso. Es un endpoint publico para usuarios autenticados (no requiere
     * roles especiales).
     *
     * @param limite cantidad de publicaciones a devolver (default 20, maximo 50)
     * @return lista de publicaciones en orden descendente por fecha de creacion
     */
    @GetMapping
    public ResponseEntity<List<PublicacionResponse>> listar(
            @RequestParam(defaultValue = "20") int limite) {
        List<Publicacion> publicaciones = listarUseCase.ejecutar(limite);
        List<PublicacionResponse> response = publicaciones.stream()
                .map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }
}
