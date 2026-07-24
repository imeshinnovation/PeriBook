package com.peribook.user.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que encapsula la validación y el formateo de direcciones de email.
 * <p>
 * Elegí un {@code record} de Java 14+ porque me da inmutabilidad, equals/hashCode
 * basados en valor y el constructor compacto que me permite inyectar validación sin
 * perder la sintaxis limpia. En DDD los Value Objects se comparan por atributos,
 * no por identidad — justo lo que un record ofrece de serie.
 * </p>
 * <p>
 * El patrón de validación no es perfecto ni pretende serlo (no valida dominios
 * internacionalizados, por ejemplo), pero cubre el 99 % de los casos reales de PeriBook
 * sin depender de una librería externa solo para esto.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
public record Email(String value) {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]{2,}$");

    /**
     * Constructor compacto del record: ejecuta las validaciones antes de asignar el campo.
     * Prefiero lanzar {@link IllegalArgumentException} aquí a permitir que un email
     * inválido cruce capas y falle en un punto más difícil de rastrear.
     */
    public Email {
        Objects.requireNonNull(value, "El email no puede ser nulo");
        if (value.isBlank()) throw new IllegalArgumentException("El email no puede estar vacío");
        if (!EMAIL_PATTERN.matcher(value).matches())
            throw new IllegalArgumentException("Formato de email inválido: " + value);
    }
}
