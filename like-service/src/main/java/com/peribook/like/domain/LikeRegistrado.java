package com.peribook.like.domain;

import java.time.Instant;
import java.util.UUID;

public record LikeRegistrado(
        UUID likeId,
        UUID publicacionId,
        UUID usuarioId,
        Instant creadoEn
) {
    public static LikeRegistrado desde(Like like) {
        return new LikeRegistrado(like.id(), like.publicacionId(), like.usuarioId(), like.creadoEn());
    }
}
<!-- 2026-07-09 -->
