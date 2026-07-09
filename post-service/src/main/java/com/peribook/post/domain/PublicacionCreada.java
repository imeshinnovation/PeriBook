package com.peribook.post.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event: se emite cuando se crea una nueva publicación.
 * Este contrato es JSON — cada servicio que lo consuma define su propio DTO.
 */
public record PublicacionCreada(
        UUID publicacionId,
        UUID autorId,
        String contenido,
        Instant creadaEn
) {
    public static PublicacionCreada desde(Publicacion p) {
        return new PublicacionCreada(p.id(), p.autorId(), p.contenido(), p.creadaEn());
    }
}
<!-- 2026-07-09 -->
