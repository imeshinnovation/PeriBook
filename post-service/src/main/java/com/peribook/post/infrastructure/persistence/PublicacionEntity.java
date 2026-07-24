package com.peribook.post.infrastructure.persistence;

import com.peribook.post.domain.Publicacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad JPA que persiste las publicaciones en la tabla "publicaciones".
 * 

 * Esta clase es el "adaptador de persistencia" en la terminologia de Hexagonal Architecture.
 * Existe unicamente para el mapeo ORM y nunca debe usarse fuera de la capa de infraestructura.
 * El dominio trabaja exclusivamente con la entidad Publicacion, que no tiene
 * anotaciones JPA ni conoce nada sobre la base de datos.
 * 

 * Decidi usar el mismo UUID como clave primaria en lugar de un ID autoincremental.
 * Esto simplifica el mapeo porque no necesito una estrategia de generacion de IDs
 * hibrida entre JPA y el dominio. El dominio genera el UUID, y JPA lo usa tal cual.
 * 

 * El constructor vacio protegido es requerido por JPA/Hibernate para la creacion de
 * instancias via reflection. Lo marque  en lugar de 
 * para que solo el framework y el mismo paquete puedan accederlo, pero las clases
 * externas (como servicios) no puedan crear entidades vacias accidentalmente.
 *
 * @author Alexander Rubio Caceres
 */
@Entity
@Table(name = "publicaciones")
public class PublicacionEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID autorId;

    @Column(nullable = false, length = 500)
    private String contenido;

    @Column(nullable = false)
    private Instant creadaEn;

    /** Constructor vacio requerido por JPA. No usar directamente. */
    protected PublicacionEntity() {}

    /**
     * Constructor privado. Solo se accede a traves del factory method
     * #fromDomain(Publicacion).
     */
    private PublicacionEntity(UUID id, UUID autorId, String contenido, Instant creadaEn) {
        this.id = id;
        this.autorId = autorId;
        this.contenido = contenido;
        this.creadaEn = creadaEn;
    }

    /**
     * Convierte una entidad de dominio a una entidad JPA para persistencia.
     *
     * @param p la entidad de dominio Publicacion
     * @return una nueva instancia de PublicacionEntity con los mismos valores
     */
    public static PublicacionEntity fromDomain(Publicacion p) {
        return new PublicacionEntity(p.id(), p.autorId(), p.contenido(), p.creadaEn());
    }

    /**
     * Convierite esta entidad JPA de vuelta a una entidad de dominio.
     * Usa el metodo UUID, String, Instant)
     * que no aplica validaciones de negocio (se asume que los datos ya fueron
     * validados al crearse).
     *
     * @return la entidad de dominio reconstituida
     */
    public Publicacion toDomain() {
        return Publicacion.reconstituir(id, autorId, contenido, creadaEn);
    }

    /** @return UUID de la publicacion */
    public UUID getId() { return id; }
    /** @return UUID del autor */
    public UUID getAutorId() { return autorId; }
    /** @return contenido textual */
    public String getContenido() { return contenido; }
    /** @return fecha de creacion */
    public Instant getCreadaEn() { return creadaEn; }
}
