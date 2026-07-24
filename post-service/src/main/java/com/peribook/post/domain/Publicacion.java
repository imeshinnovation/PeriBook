package com.peribook.post.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Agregado raiz del bounded context de publicaciones dentro del dominio de PeriBook.
 * <p>
 * Esta es posiblemente la clase mas importante del post-service. Decidi que {@code Publicacion}
 * fuera el agregado raiz siguiendo los principios de DDD (Domain-Driven Design) de Eric Evans.
 * Esto significa que toda operacion sobre una publicacion pasa por esta entidad, que garantiza
 * sus invariantes antes de persistir cualquier cambio.
 * <p>
 /> Los invariantes que protege son:
 * <ul>
 *   <li>El autor (autorId) no puede ser nulo — toda publicacion pertenece a alguien</li>
 *   <li>El contenido no puede ser nulo ni vacio despues de hacer trim</li>
 *   <li>El contenido no puede exceder los 500 caracteres — decidi este limite para equilibrar
 *       la libertad de expresion con la estabilidad del almacenamiento y la red</li>
 * </ul>
 * <p>
 * Para la identidad use {@code UUID} en lugar de un ID autoincremental. Los UUID permiten
 * generar identificadores en el dominio sin depender de la base de datos, lo cual es esencial
 * en una arquitectura de microservicios donde diferentes servicios pueden necesitar referenciar
 * la misma entidad sin acoplamiento de esquema.
 *
 * @author Alexander Rubio Caceres
 */
public class Publicacion {

    /** Identificador unico de la publicacion (generado en dominio, no en BD). */
    private final UUID id;
    /** Identificador del autor (viene del JWT, se pasa como UUID). */
    private final UUID autorId;
    /** Contenido textual de la publicacion, validado entre 1 y 500 caracteres. */
    private final String contenido;
    /** Marca temporal de cuando se creo (se asigna en dominio, no en BD). */
    private final Instant creadaEn;

    /**
     * Constructor privado. La unica forma de obtener una instancia es a traves de
     * los metodos factory {@link #crear(UUID, String)} o {@link #reconstituir(UUID, UUID, String, Instant)}.
     * Esto asegura que toda Publicacion en el sistema paso por las validaciones
     * de negocio correspondientes.
     */
    private Publicacion(UUID id, UUID autorId, String contenido, Instant creadaEn) {
        this.id = id;
        this.autorId = autorId;
        this.contenido = contenido;
        this.creadaEn = creadaEn;
    }

    /**
     * Factory method para crear una publicacion nueva.
     * <p>
     * Aqui se aplican todas las validaciones de negocio antes de que la entidad exista:
     * <ul>
     *   <li>Null safety con {@link Objects#requireNonNull}</li>
     *   <li>Contenido no vacio despues de limpiar espacios</li>
     *   <li>Longitud maxima de 500 caracteres</li>
     * </ul>
     * El ID se genera con {@link UUID#randomUUID()} y la fecha con {@link Instant#now()}
     * para que el dominio sea autocontenido y no dependa de la capa de infraestructura
     * para estos valores.
     *
     * @param autorId   identificador del autor (obtenido del token JWT)
     * @param contenido texto de la publicacion (entre 1 y 500 caracteres)
     * @return una nueva instancia de Publicacion con todos sus invariantes validados
     * @throws NullPointerException     si autorId o contenido son null
     * @throws IllegalArgumentException si el contenido esta vacio o excede los 500 caracteres
     */
    public static Publicacion crear(UUID autorId, String contenido) {
        Objects.requireNonNull(autorId, "autorId no puede ser nulo");
        Objects.requireNonNull(contenido, "contenido no puede ser nulo");
        String trimmed = contenido.trim();
        if (trimmed.isEmpty()) throw new IllegalArgumentException("El contenido no puede estar vacio");
        if (trimmed.length() > 500) throw new IllegalArgumentException("El contenido excede los 500 caracteres");
        return new Publicacion(UUID.randomUUID(), autorId, trimmed, Instant.now());
    }

    /**
     * Factory method para reconstituir una publicacion desde persistencia.
     * <p>
     * Este metodo se usa exclusivamente cuando se lee una publicacion existente desde
     * la base de datos. A diferencia de {@link #crear(UUID, String)}, no genera un nuevo
     * ID ni una nueva fecha — preserva los valores originales que ya estan en la BD.
     * Tampoco valida el contenido porque asumimos que si ya estaba persistido, ya paso
     * las validaciones en su momento (aunque en teoria podria haber migraciones de datos
     * que requieran relajar esta regla).
     *
     * @param id        UUID persistido (no se genera uno nuevo)
     * @param autorId   identificador del autor persistido
     * @param contenido contenido persistido
     * @param creadaEn  fecha original de creacion
     * @return la entidad reconstituida
     */
    public static Publicacion reconstituir(UUID id, UUID autorId, String contenido, Instant creadaEn) {
        Objects.requireNonNull(id, "id no puede ser nulo");
        return new Publicacion(id, autorId, contenido, creadaEn);
    }

    /** @return identificador unico de la publicacion */
    public UUID id() { return id; }
    /** @return identificador del autor de la publicacion */
    public UUID autorId() { return autorId; }
    /** @return contenido textual de la publicacion */
    public String contenido() { return contenido; }
    /** @return fecha y hora de creacion de la publicacion */
    public Instant creadaEn() { return creadaEn; }

    /**
     * Compara publicaciones por identidad (UUID), no por valor.
     * <p>
     * Dos publicaciones son iguales si tienen el mismo ID, independientemente de su
     * contenido o fecha. Esto refleja que en nuestro dominio la identidad es absoluta
     * — si dos objetos representan la misma publicacion del mundo real, deben tener
     * el mismo UUID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publicacion p)) return false;
        return id.equals(p.id);
    }

    /** Hashcode basado unicamente en el UUID, consistente con {@link #equals(Object)}. */
    @Override
    public int hashCode() { return id.hashCode(); }
}
