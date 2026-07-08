package com.peribook.post.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Agregado raíz del bounded context de publicaciones.
 */
public class Publicacion {

    private final UUID id;
    private final UUID autorId;
    private final String contenido;
    private final Instant creadaEn;

    private Publicacion(UUID id, UUID autorId, String contenido, Instant creadaEn) {
        this.id = id;
        this.autorId = autorId;
        this.contenido = contenido;
        this.creadaEn = creadaEn;
    }

    public static Publicacion crear(UUID autorId, String contenido) {
        Objects.requireNonNull(autorId, "autorId no puede ser nulo");
        Objects.requireNonNull(contenido, "contenido no puede ser nulo");
        String trimmed = contenido.trim();
        if (trimmed.isEmpty()) throw new IllegalArgumentException("El contenido no puede estar vacío");
        if (trimmed.length() > 500) throw new IllegalArgumentException("El contenido excede los 500 caracteres");
        return new Publicacion(UUID.randomUUID(), autorId, trimmed, Instant.now());
    }

    public static Publicacion reconstituir(UUID id, UUID autorId, String contenido, Instant creadaEn) {
        Objects.requireNonNull(id, "id no puede ser nulo");
        return new Publicacion(id, autorId, contenido, creadaEn);
    }

    public UUID id() { return id; }
    public UUID autorId() { return autorId; }
    public String contenido() { return contenido; }
    public Instant creadaEn() { return creadaEn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publicacion p)) return false;
        return id.equals(p.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
