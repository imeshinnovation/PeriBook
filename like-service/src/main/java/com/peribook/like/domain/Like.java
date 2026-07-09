package com.peribook.like.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Agregado raíz. La combinación (publicacionId, usuarioId) es única —
 * un usuario no puede dar like dos veces a la misma publicación.
 */
public class Like {

    private final UUID id;
    private final UUID publicacionId;
    private final UUID usuarioId;
    private final Instant creadoEn;

    private Like(UUID id, UUID publicacionId, UUID usuarioId, Instant creadoEn) {
        this.id = id;
        this.publicacionId = publicacionId;
        this.usuarioId = usuarioId;
        this.creadoEn = creadoEn;
    }

    public static Like dar(UUID publicacionId, UUID usuarioId) {
        Objects.requireNonNull(publicacionId, "publicacionId no puede ser nulo");
        Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo");
        return new Like(UUID.randomUUID(), publicacionId, usuarioId, Instant.now());
    }

    public static Like reconstituir(UUID id, UUID publicacionId, UUID usuarioId, Instant creadoEn) {
        Objects.requireNonNull(id, "id no puede ser nulo");
        return new Like(id, publicacionId, usuarioId, creadoEn);
    }

    public UUID id() { return id; }
    public UUID publicacionId() { return publicacionId; }
    public UUID usuarioId() { return usuarioId; }
    public Instant creadoEn() { return creadoEn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Like like)) return false;
        return id.equals(like.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
<!-- 2026-07-09 -->
