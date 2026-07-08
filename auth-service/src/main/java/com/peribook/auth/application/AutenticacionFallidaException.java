package com.peribook.auth.application;

/**
 * Excepción de dominio lanzada cuando las credenciales no son válidas.
 * No revela si falló el email o la contraseña (previene enumeración de usuarios).
 */
public class AutenticacionFallidaException extends RuntimeException {

    public AutenticacionFallidaException(String message) {
        super(message);
    }
}
