package com.peribook.user.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Agregado raíz del bounded context de perfiles de usuario.
 * Cada Perfil está vinculado 1:1 a un Usuario del bounded context de auth.
 */
public class Perfil {

    private final UUID id;
    private final UUID usuarioId;  // referencia al Usuario de auth-service
    private final Email email;
    private final String alias;
    private final String nombres;
    private final String apellidos;
    private final LocalDate fechaNacimiento;

    private Perfil(UUID id, UUID usuarioId, Email email, String alias,
                   String nombres, String apellidos, LocalDate fechaNacimiento) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.email = email;
        this.alias = alias;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
    }

    public static Perfil crear(UUID usuarioId, Email email, String alias,
                               String nombres, String apellidos, LocalDate fechaNacimiento) {
        Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo");
        Objects.requireNonNull(nombres, "nombres no puede ser nulo");
        Objects.requireNonNull(apellidos, "apellidos no puede ser nulo");
        if (nombres.isBlank()) throw new IllegalArgumentException("nombres no puede estar vacío");
        if (apellidos.isBlank()) throw new IllegalArgumentException("apellidos no puede estar vacío");
        return new Perfil(UUID.randomUUID(), usuarioId, email,
                alias != null ? alias.trim() : "", nombres.trim(), apellidos.trim(), fechaNacimiento);
    }

    public static Perfil reconstituir(UUID id, UUID usuarioId, Email email, String alias,
                                      String nombres, String apellidos, LocalDate fechaNacimiento) {
        Objects.requireNonNull(id, "id no puede ser nulo");
        return new Perfil(id, usuarioId, email, alias, nombres, apellidos, fechaNacimiento);
    }

    public UUID id() { return id; }
    public UUID usuarioId() { return usuarioId; }
    public Email email() { return email; }
    public String alias() { return alias; }
    public String nombres() { return nombres; }
    public String apellidos() { return apellidos; }
    public LocalDate fechaNacimiento() { return fechaNacimiento; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Perfil perfil)) return false;
        return id.equals(perfil.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
