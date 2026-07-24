package com.peribook.auth.application;

/**
 * Excepción lanzada cuando las credenciales de autenticación no son válidas.
 * 

 * Es importante notar que el mensaje de error nunca diferencia entre "email
 * no encontrado" y "contraseña incorrecta". Esto es deliberado: si el mensaje
 * dijera "email no registrado", un atacante podría enumerar qué emails están
 * registrados en PeriBook probando uno tras otro hasta recibir el mensaje de
 * "contraseña incorrecta". Es un vector de ataque conocido (enumeración de
 * usuarios) y lo mitigo desde la excepción misma.
 * 
 * 

 * Extiendo RuntimeException y no una excepción checked porque Spring
 * maneja este error mediante un  en el controller,
 * y no quiero forzar a cada llamante a capturar algo que no va a manejar.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
public class AutenticacionFallidaException extends RuntimeException {

    /**
     * @param message mensaje genérico, nunca revela si falló el email o password
     */
    public AutenticacionFallidaException(String message) {
        super(message);
    }
}
