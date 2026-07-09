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

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private static final Logger log = LoggerFactory.getLogger(LikeController.class);
    private final DarLikeUseCase darLikeUseCase;

    public LikeController(DarLikeUseCase darLikeUseCase) {
        this.darLikeUseCase = darLikeUseCase;
    }

    @GetMapping("/{publicacionId}/count")
    public ResponseEntity<java.util.Map<String, Long>> contarLikes(@PathVariable UUID publicacionId) {
        long total = darLikeUseCase.contarPorPublicacion(publicacionId);
        return ResponseEntity.ok(java.util.Map.of("total", total));
    }

    @PostMapping
    public ResponseEntity<LikeResponse> darLike(
            @RequestParam UUID publicacionId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID usuarioId = UUID.fromString(jwt.getClaimAsString("userId"));
        log.info("Like: publicacion={}, usuario={}", publicacionId, usuarioId);

        DarLikeUseCase.Resultado resultado = darLikeUseCase.ejecutar(publicacionId, usuarioId);

        LikeResponse response = new LikeResponse(
                resultado.like().id().toString(),
                publicacionId.toString(),
                0, // El contador se obtiene del like-service más adelante (Fase 6 BFF)
                resultado.esNuevo()
        );

        HttpStatus status = resultado.esNuevo() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
}
<!-- 2026-07-09 -->
