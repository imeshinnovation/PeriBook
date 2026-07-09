package com.peribook.user.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]{2,}$");

    public Email {
        Objects.requireNonNull(value, "El email no puede ser nulo");
        if (value.isBlank()) throw new IllegalArgumentException("El email no puede estar vacío");
        if (!EMAIL_PATTERN.matcher(value).matches())
            throw new IllegalArgumentException("Formato de email inválido: " + value);
    }
}
<!-- 2026-07-09 -->
