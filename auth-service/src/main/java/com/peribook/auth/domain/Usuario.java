package com.peribook.auth.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Agregado raíz del bounded context de autenticación.
 * Entidad con identidad propia (UUID).
 */
public class Usuario {

    private final UUID id;
    private final Email email;
    private final Password password;
    private final String alias;

    private Usuario(UUID id, Email email, Password password, String alias) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.alias = alias;
    }

    /**
     * Crea un nuevo usuario con UUID generado y contraseña ya hasheada.
     */
    public static Usuario registrar(Email email, Password password, String alias) {
        Objects.requireNonNull(alias, "El alias no puede ser nulo");
        if (alias.isBlank()) {
            throw new IllegalArgumentException("El alias no puede estar vacío");
        }
        return new Usuario(UUID.randomUUID(), email, password, alias.trim());
    }

    /**
     * Reconstruye un usuario desde persistencia (con ID conocido).
     */
    public static Usuario reconstituir(UUID id, Email email, Password password, String alias) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        return new Usuario(id, email, password, alias.trim());
    }

    /**
     * Intenta autenticar con contraseña en texto plano. Devuelve true si coincide.
     */
    public boolean autenticar(String rawPassword) {
        return password.matches(rawPassword);
    }

    // ── Getters ─────────────────────────────────────────────

    public UUID id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public String alias() {
        return alias;
    }

    public Password password() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
<!-- 2026-07-09 -->
