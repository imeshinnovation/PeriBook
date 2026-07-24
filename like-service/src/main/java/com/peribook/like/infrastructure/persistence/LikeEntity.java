package com.peribook.like.infrastructure.persistence;

import com.peribook.like.domain.Like;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad JPA que persiste un Like en la base de datos.
 * 

 * Decidi separar la entidad de persistencia () del agregado
 * de dominio () para no contaminar el nucleo con anotaciones de
 * JPA. Esta separacion me da libertad de cambiar de ORM o incluso de base de
 * datos sin afectar el modelo de dominio.
 * 
 * 

 * La  sobre (publicacionId, usuarioId) refuerza a nivel
 * de base de datos la invariante de negocio: un usuario no puede dar like dos
 * veces a la misma publicacion.
 * 
 *
 * @author Alexander Rubio Caceres
 */
@Entity
@Table(name = "likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"publicacionId", "usuarioId"}))
public class LikeEntity {

    @Id private UUID id;
    @Column(nullable = false) private UUID publicacionId;
    @Column(nullable = false) private UUID usuarioId;
    @Column(nullable = false) private Instant creadoEn;

    // Constructor vacio requerido por JPA (Hibernate lo necesita para
    // instanciar via reflexion). Lo mantengo protected para que solo el
    // ORM y clases del mismo paquete accedan.
    protected LikeEntity() {}

    private LikeEntity(UUID id, UUID publicacionId, UUID usuarioId, Instant creadoEn) {
        this.id = id;
        this.publicacionId = publicacionId;
        this.usuarioId = usuarioId;
        this.creadoEn = creadoEn;
    }

    /**
     * Convierte un agregado de dominio Like en la entidad JPA.
     * Metodo de fabrica estatico para mantener la creacion simple y centralizada.
     */
    public static LikeEntity fromDomain(Like l) {
        return new LikeEntity(l.id(), l.publicacionId(), l.usuarioId(), l.creadoEn());
    }

    /**
     * Reconstituye un Like de dominio desde esta entidad.
     * Uso Like#reconstituir para preservar el timestamp original.
     */
    public Like toDomain() {
        return Like.reconstituir(id, publicacionId, usuarioId, creadoEn);
    }

    // Getters requeridos por Jackson / Hibernate para serializacion y
    // busqueda por propiedades. Prefiero getters con prefijo tradicional
    // aqui (no estilo record) porque JPA los necesita para el mapping.
    public UUID getId() { return id; }
    public UUID getPublicacionId() { return publicacionId; }
    public UUID getUsuarioId() { return usuarioId; }
    public Instant getCreadoEn() { return creadoEn; }
}
