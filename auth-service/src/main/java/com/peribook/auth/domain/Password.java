package com.peribook.auth.domain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

/**
 * Value Object inmutable que encapsula un password hasheado con BCrypt.
 * La responsabilidad de hashear está aquí porque BCrypt es un algoritmo
 * estándar, no una dependencia de framework.
 */
public record Password(String hash) {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public Password {
        Objects.requireNonNull(hash, "El hash del password no puede ser nulo");
        if (hash.isBlank()) {
            throw new IllegalArgumentException("El hash del password no puede estar vacío");
        }
    }

    /**
     * Factory: crea un Password a partir de texto plano, aplicando BCrypt.
     */
    public static Password fromRaw(String rawPassword) {
        Objects.requireNonNull(rawPassword, "El password en texto plano no puede ser nulo");
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("El password debe tener al menos 8 caracteres");
        }
        return new Password(ENCODER.encode(rawPassword));
    }

    /**
     * Verifica si el texto plano coincide con el hash almacenado.
     */
    public boolean matches(String rawPassword) {
        Objects.requireNonNull(rawPassword, "El password a verificar no puede ser nulo");
        return ENCODER.matches(rawPassword, this.hash);
    }
}
<!-- 2026-07-09 -->
