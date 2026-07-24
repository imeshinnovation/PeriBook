package com.peribook.auth.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object inmutable que representa un email de usuario validado.
 * 

 * Elegí implementarlo como un  de Java 21 porque los records
 * me dan de forma gratuita: inmutabilidad, /
 * basados en el valor, constructor canónico y un formato declarativo que
 * comunica la intención sin boilerplate. En DDD los Value Objects deben ser
 * inmutables y compararse por valor — justo lo que un  garantiza.
 * 
 * 

 * La validación del formato ocurre en el constructor compacto, así que es
 * imposible crear una instancia con un email inválido. Esto aplica el principio
 * de "fail fast" en la frontera del dominio: cualquier email que llegue aquí
 * ya pasó por validación de infraestructura (Bean Validation en el controller),
 * pero no confío en eso — el dominio debe protegerse solo.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
public record Email(String value) {

    /**
     * Regex que uso para validar el formato del email.
     * No es perfecta ni cubre todos los casos del RFC 5321 — para eso
     * necesitaría una librería como Apache Commons Validator — pero cubre
     * el ~99% de los casos reales y es más ligera. Si el negocio requiere
     * validación exhaustiva algún día, es fácil de reemplazar.
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]{2,}$");

    /**
     * Constructor compacto del record.
     * Valida que el email no sea nulo, no esté vacío y cumpla el patrón.
     * Al lanzar NullPointerException con mensaje para nulos y
     * IllegalArgumentException para formato incorrecto, el llamante
     * puede distinguir el tipo de error si lo necesita.
     */
    public Email {
        Objects.requireNonNull(value, "El email no puede ser nulo");
        if (value.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Formato de email inválido: " + value);
        }
    }
}
