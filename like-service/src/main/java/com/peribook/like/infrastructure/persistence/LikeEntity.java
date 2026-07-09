package com.peribook.like.infrastructure.persistence;

import com.peribook.like.domain.Like;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"publicacionId", "usuarioId"}))
public class LikeEntity {

    @Id private UUID id;
    @Column(nullable = false) private UUID publicacionId;
    @Column(nullable = false) private UUID usuarioId;
    @Column(nullable = false) private Instant creadoEn;

    protected LikeEntity() {}

    private LikeEntity(UUID id, UUID publicacionId, UUID usuarioId, Instant creadoEn) {
        this.id = id; this.publicacionId = publicacionId; this.usuarioId = usuarioId; this.creadoEn = creadoEn;
    }

    public static LikeEntity fromDomain(Like l) {
        return new LikeEntity(l.id(), l.publicacionId(), l.usuarioId(), l.creadoEn());
    }

    public Like toDomain() {
        return Like.reconstituir(id, publicacionId, usuarioId, creadoEn);
    }

    public UUID getId() { return id; }
    public UUID getPublicacionId() { return publicacionId; }
    public UUID getUsuarioId() { return usuarioId; }
    public Instant getCreadoEn() { return creadoEn; }
}
<!-- 2026-07-09 -->
