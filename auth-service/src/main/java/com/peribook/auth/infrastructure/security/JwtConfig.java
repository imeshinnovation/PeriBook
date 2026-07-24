package com.peribook.auth.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuración de JWT injectada desde  mediante
 * Spring Boot .
 * 

 * Es un  inmutable con binding automático a las propiedades
 * , , 
 * y . Uso  en el YAML y
 * Spring Boot hace la correspondencia automática con los campos del record.
 * 
 * 

 * Decidí agrupar las rutas de las claves RSA en un sub-record RsaKeys
 * para mantener la jerarquía de propiedades limpia y porque ambas rutas están
 * conceptualmente relacionadas. Si en el futuro agrego más configuraciones
 * JWT (algoritmos alternativos, rotación de claves, etc.), el record crece
 * de forma ordenada.
 * 
 * 

 * La habilitación de este record como bean se hace en
 * com.peribook.auth.AuthServiceApplication con
 * .
 * 
 *
 * @author Alexander Rubio Cáceres
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
        /** Emisor del token (normalmente la URL del servicio) */
        String issuer,

        /** Duración de validez del token (ej: 1h, 24h) */
        Duration expiration,

        /** Rutas a las claves RSA para firma y verificación */
        RsaKeys rsa
) {
    /**
     * Rutas del sistema de archivos donde están las claves RSA.
     * 

     * En producción las claves se montan como secretos de Docker Swarm en
     * . En desarrollo local se cargan desde el classpath.
     * La lógica de carga está en SecurityConfig#loadKeyBytes(String).
     * 
     */
    public record RsaKeys(
            /** Ruta a la clave pública en formato PEM */
            String publicKeyPath,

            /** Ruta a la clave privada en formato PEM */
            String privateKeyPath
    ) {}
}
