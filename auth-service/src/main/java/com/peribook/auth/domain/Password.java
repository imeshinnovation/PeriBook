package com.peribook.auth.domain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

/**
 * Value Object inmutable que encapsula una contraseña ya hasheada con BCrypt.
 * 

 * Tomé la decisión consciente de poner BCrypt directamente aquí en el dominio
 * y no en la capa de infraestructura. Normalmente uno diría que el hashing es
 * un detalle técnico, pero en este caso BCrypt  de
 * Spring Security es tan ubicuo como estándar (NIST recomienda BCrypt, el
 * OWASP también), y tratarlo como un detalle de infraestructura intercambiable
 * me pareció sobrediseño. Si mañana quisiera cambiar a Argon2, el cambio
 * estaría aislado en esta clase — no hay riesgo de fuga hacia el dominio.
 * 
 * 

 * El algoritmo BCrypt incluye un salt automático, así que dos contraseñas
 * iguales producen hashes distintos — eso mitiga ataques de rainbow tables
 * sin que yo tenga que preocuparme por gestionar salts explícitamente.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
public record Password(String hash) {

    /**
     * Instancia única del codificador BCrypt.
     * La hago  porque  es
     * thread-safe y no tiene estado mutable relevante, así que reutilizar la
     * instancia evita el overhead de crear una nueva cada vez.
     */
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * Constructor compacto del record.
     * Valida que el hash no sea nulo ni vacío. No valido el formato del hash
     * porque BCrypt tiene un prefijo  fácil de verificar
     * pero prefiero no acoplar la validación estructural al formato actual;
     * el día que migre a otro algoritmo esta validación estorbaría.
     */
    public Password {
        Objects.requireNonNull(hash, "El hash del password no puede ser nulo");
        if (hash.isBlank()) {
            throw new IllegalArgumentException("El hash del password no puede estar vacío");
        }
    }

    /**
     * Crea un Password a partir de una contraseña en texto plano,
     * aplicando BCrypt automáticamente.
     * 

     * Esta es la única vía por la que el sistema acepta contraseñas en claro.
     * Decidí que la validación de longitud mínima (8 caracteres) esté aquí
     * y no en el controller, porque es una regla de dominio: la política de
     * seguridad de PeriBook exige mínimo 8 caracteres. Si la política cambia
     * (por ejemplo, a 12), se cambia aquí y el cambio repercute en toda la
     * aplicación sin tener que tocar controllers ni DTOs.
     * 
     *
     * @param rawPassword contraseña en texto plano (min. 8 caracteres)
     * @return un nuevo  con el hash BCrypt
     * @throws NullPointerException     si rawPassword es nulo
     * @throws IllegalArgumentException si rawPassword tiene menos de 8 caracteres
     */
    public static Password fromRaw(String rawPassword) {
        Objects.requireNonNull(rawPassword, "El password en texto plano no puede ser nulo");
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("El password debe tener al menos 8 caracteres");
        }
        return new Password(ENCODER.encode(rawPassword));
    }

    /**
     * Verifica si la contraseña en texto plano coincide con el hash almacenado.
     * 

     * Delega en , que compara el texto
     * plano contra el hash usando el salt extraído del propio hash — así no
     * necesito almacenar el salt por separado.
     * 
     *
     * @param rawPassword contraseña en texto plano a verificar
     * @return  si coincide,  en caso contrario
     * @throws NullPointerException si rawPassword es nulo
     */
    public boolean matches(String rawPassword) {
        Objects.requireNonNull(rawPassword, "El password a verificar no puede ser nulo");
        return ENCODER.matches(rawPassword, this.hash);
    }
}
