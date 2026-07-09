package com.peribook.like.domain;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository {
    Like save(Like like);
    Optional<Like> buscarPorPublicacionYUsuario(UUID publicacionId, UUID usuarioId);
    long contarPorPublicacion(UUID publicacionId);
}
