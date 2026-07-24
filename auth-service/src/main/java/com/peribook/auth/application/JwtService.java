package com.peribook.auth.application;

/**
 * Puerto (interfaz) para la generación de tokens JWT.
 * 

 * Pertenece a la capa de aplicación porque la generación de tokens es un
 * servicio de aplicación, no una regla de dominio. La interfaz se define
 * aquí para mantener la Inversión de Dependencias, pero la implementación
 * concreta (RS256 con Nimbus / jjwt) vive en infraestructura.
 * 
 * 

 * Decidí que la interfaz solo tenga un método . La
 * validación/decodificación de tokens entrantes la maneja Spring Security
 * directamente mediante el  configurado en la capa de
 * seguridad. No mezclo responsabilidades: este servicio solo emite tokens,
 * el framework los verifica.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
public interface JwtService {

    /**
     * Genera un token JWT firmado para un usuario autenticado.
     *
     * @param userId ID único del usuario (va como claim personalizado)
     * @param email  email del usuario (se usa como el  del token)
     * @return token JWT compacto (string) listo para enviar al cliente
     */
    String generate(String userId, String email);
}
