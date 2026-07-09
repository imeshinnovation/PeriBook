package com.peribook.post.infrastructure.persistence;

import com.peribook.post.domain.Publicacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "publicaciones")
public class PublicacionEntity {

    @Id private UUID id;
    @Column(nullable = false) private UUID autorId;
    @Column(nullable = false, length = 500) private String contenido;
    @Column(nullable = false) private Instant creadaEn;

    protected PublicacionEntity() {}

    private PublicacionEntity(UUID id, UUID autorId, String contenido, Instant creadaEn) {
        this.id = id; this.autorId = autorId; this.contenido = contenido; this.creadaEn = creadaEn;
    }

    public static PublicacionEntity fromDomain(Publicacion p) {
        return new PublicacionEntity(p.id(), p.autorId(), p.contenido(), p.creadaEn());
    }

    public Publicacion toDomain() {
        return Publicacion.reconstituir(id, autorId, contenido, creadaEn);
    }

    public UUID getId() { return id; }
    public UUID getAutorId() { return autorId; }
    public String getContenido() { return contenido; }
    public Instant getCreadaEn() { return creadaEn; }
}
<!-- 2026-07-09 -->
