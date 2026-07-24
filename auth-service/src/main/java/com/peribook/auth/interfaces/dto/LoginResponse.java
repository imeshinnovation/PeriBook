package com.peribook.auth.interfaces.dto;

/**
 * DTO de salida para la respuesta de login exitoso.
 * 

 * Es un  inmutable que se serializa automáticamente a JSON
 * por Jackson (gracias al módulo de Java Records de Spring Boot 3.x).
 * No incluyo el email en la respuesta por privacidad — el frontend ya lo
 * conoce porque el usuario lo escribió, y exponerlo en la respuesta no
 * aporta valor.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
public record LoginResponse(
        /** Token JWT firmado con RS256 que el cliente debe usar en adelante */
        String token,

        /** ID único del usuario autenticado */
        String userId,

        /** Alias público del usuario */
        String alias
) {}
