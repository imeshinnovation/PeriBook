package com.peribook.auth.application;

/**
 * Puerto para la generación de JWT.
 * La implementación concreta (RS256) vive en infrastructure/security.
 */
public interface JwtService {

    /**
     * Genera un JWT firmado con RS256.
     *
     * @param userId ID del usuario autenticado
     * @param email  email del usuario (va en el subject del token)
     * @return token JWT compacto
     */
    String generate(String userId, String email);
}
