package com.peribook.user.infrastructure.persistence;

import com.peribook.user.domain.Email;
import com.peribook.user.domain.Perfil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla {@code perfiles} en la base de datos.
 * <p>
 * Esta clase pertenece exclusivamente a la capa de infraestructura — el dominio
 * no sabe que JPA existe. La conversión bidireccional entre {@link PerfilEntity}
 * y {@link Perfil} ocurre en {@link #fromDomain(Perfil)} y {@link #toDomain()},
 * manteniendo el dominio limpio de anotaciones Jakarta Persistence.
 * </p>
 * <p>
 * Notar que {@code email} aquí es un {@code String} simple, no un {@link Email}.
 * Esto es deliberado: el Value Object {@code Email} pertenece al dominio y su
 * validación ya ocurrió antes de llegar aquí. La base de datos guarda el valor
 * plano; al reconstituir, {@code toDomain()} vuelve a crear el Value Object.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
@Entity
@Table(name = "perfiles")
public class PerfilEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID usuarioId;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, length = 50)
    private String alias;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    /** Opcional. No lleva {@code @Column(nullable = false)} porque puede ser null. */
    private LocalDate fechaNacimiento;

    /**
     * Constructor protegido requerido por JPA (Hibernate lo usa via reflexión).
     * No es público porque no quiero que nadie en el código cree entidades
     * vacías — toda creación debe pasar por {@link #fromDomain(Perfil)}.
     */
    protected PerfilEntity() {}

    /**
     * Constructor privado usado internamente por {@link #fromDomain(Perfil)}.
     */
    private PerfilEntity(UUID id, UUID usuarioId, String email, String alias,
                         String nombres, String apellidos, LocalDate fechaNacimiento) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.email = email;
        this.alias = alias;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Convierte un agregado {@link Perfil} (dominio) en una {@link PerfilEntity}
     * (JPA) para persistencia. Extrae el valor plano del Value Object {@link Email}.
     *
     * @param p Agregado de dominio
     * @return Entidad JPA lista para persistir
     */
    public static PerfilEntity fromDomain(Perfil p) {
        return new PerfilEntity(p.id(), p.usuarioId(), p.email().value(),
                p.alias(), p.nombres(), p.apellidos(), p.fechaNacimiento());
    }

    /**
     * Reconstituye un {@link Perfil} (dominio) desde esta entidad JPA.
     * Vuelve a crear el Value Object {@link Email} aplicando su validación.
     *
     * @return Agregado de dominio reconstituido
     */
    public Perfil toDomain() {
        return Perfil.reconstituir(id, usuarioId, new Email(email),
                alias, nombres, apellidos, fechaNacimiento);
    }

    // -- Getters estándar (necesarios para JPA y serialización)

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getEmail() { return email; }
    public String getAlias() { return alias; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
}
