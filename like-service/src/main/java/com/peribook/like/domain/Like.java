package com.peribook.like.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Agregado raíz del dominio {@code Like}.
 * <p>
 * Hecho con una combinación de (publicacionId, usuarioId) como invariante de negocio:
 * un usuario no puede registrar dos veces un "me gusta" sobre la misma publicación.
 * Opté por un constructor privado y dos fábricas estáticas ({@link #dar} y
 * {@link #reconstituir}) para separar claramente la creación desde intención (nuevo
 * evento) de la reconstrucción desde persistencia.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
public class Like {

    private final UUID id;
    private final UUID publicacionId;
    private final UUID usuarioId;
    private final Instant creadoEn;

    // Constructor privado: obligo a pasar por los métodos estáticos que validan.
    private Like(UUID id, UUID publicacionId, UUID usuarioId, Instant creadoEn) {
        this.id = id;
        this.publicacionId = publicacionId;
        this.usuarioId = usuarioId;
        this.creadoEn = creadoEn;
    }

    /**
     * Crea un nuevo {@code Like} desde cero — genera un ID aleatorio y timestamps
     * con el momento actual. Es la fábrica que se usa en el caso de uso cuando el
     * usuario da like por primera vez.
     *
     * @param publicacionId publicación a la que se da like
     * @param usuarioId      usuario que da el like
     * @return una nueva instancia de Like
     * @throws NullPointerException si alguno de los argumentos es nulo
     */
    public static Like dar(UUID publicacionId, UUID usuarioId) {
        Objects.requireNonNull(publicacionId, "publicacionId no puede ser nulo");
        Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo");
        return new Like(UUID.randomUUID(), publicacionId, usuarioId, Instant.now());
    }

    /**
     * Reconstituye un {@code Like} desde el repositorio con todos los valores ya
     * conocidos (id, creadoEn). Decidí que este método no genere el timestamp porque
     * al recuperar de base de datos quiero preservar exactamente el momento original
     * en que se registró el like.
     *
     * @param id             identificador único persistido
     * @param publicacionId  publicación asociada
     * @param usuarioId      usuario asociado
     * @param creadoEn       timestamp original
     * @return una instancia de Like con los valores provistos
     * @throws NullPointerException si el id es nulo
     */
    public static Like reconstituir(UUID id, UUID publicacionId, UUID usuarioId, Instant creadoEn) {
        Objects.requireNonNull(id, "id no puede ser nulo");
        return new Like(id, publicacionId, usuarioId, creadoEn);
    }

    // Accessors con estilo record-like, sin prefijo "get", para mantener el mismo
    // lenguaje ubicuo que usamos en el dominio.
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
