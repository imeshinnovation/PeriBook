package com.peribook.like.interfaces;

import com.peribook.like.application.DarLikeUseCase;
import com.peribook.like.interfaces.dto.LikeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST del microservicio Like.
 * <p>
 * Expone los endpoints para dar like y consultar el contador de likes de una
 * publicacion. Decidi mantener el controlador delgado: solo valida parametros
 * de entrada, delega en el caso de uso y construye la respuesta HTTP. Toda la
 * logica de negocio queda en la capa de aplicacion/dominio.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private static final Logger log = LoggerFactory.getLogger(LikeController.class);
    private final DarLikeUseCase darLikeUseCase;

    public LikeController(DarLikeUseCase darLikeUseCase) {
        this.darLikeUseCase = darLikeUseCase;
    }

    /**
     * GET /api/likes/{publicacionId}/count
     * <p>
     * Devuelve el numero total de likes de una publicacion. Es un endpoint
     * publico dentro del servicio (aunque protegido por JWT a nivel global)
     * porque cualquier usuario autenticado puede ver los contadores.
     * </p>
     */
    @GetMapping("/{publicacionId}/count")
    public ResponseEntity<java.util.Map<String, Long>> contarLikes(@PathVariable UUID publicacionId) {
        long total = darLikeUseCase.contarPorPublicacion(publicacionId);
        return ResponseEntity.ok(java.util.Map.of("total", total));
    }

    /**
     * POST /api/likes
     * <p>
     * Registra un like de un usuario a una publicacion. El {@code usuarioId}
     * se extrae del token JWT (claim "userId"), asi que el cliente solo envia
     * el {@code publicacionId} como query param. Esto evita que un cliente
     * malicioso pueda suplantar a otro usuario.
     * </p>
     * <p>
     * La respuesta distingue entre CREATED (like nuevo) y OK (ya existia,
     * operacion idempotente).
     * </p>
     */
    @PostMapping
    public ResponseEntity<LikeResponse> darLike(
            @RequestParam UUID publicacionId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID usuarioId = UUID.fromString(jwt.getClaimAsString("userId"));
        log.info("Like: publicacion={}, usuario={}", publicacionId, usuarioId);

        DarLikeUseCase.Resultado resultado = darLikeUseCase.ejecutar(publicacionId, usuarioId);

        // totalLikes se deja en 0 porque el like-service no tiene la responsabilidad
        // de contar en este punto; el BFF lo enriquece en una llamada posterior.
        LikeResponse response = new LikeResponse(
                resultado.like().id().toString(),
                publicacionId.toString(),
                0,
                resultado.esNuevo()
        );

        HttpStatus status = resultado.esNuevo() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
}
