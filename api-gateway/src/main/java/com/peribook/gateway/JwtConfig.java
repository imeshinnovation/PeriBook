package com.peribook.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuración JWT externalizada, mapeada directamente desde {@code jwt.*}
 * en el application.yml.
 * <p>
 * Usé un {@code record} de Java 21 por varias razones: es inmutable por
 * naturaleza (lo que evita que alguien accidentalmente modifique la
 * configuración en runtime), genera los accessors de forma implícita, y el
 * código queda mucho más limpio que una clase con getters, setters y
 * constructores. En un gateway donde la seguridad es crítica, la inmutabilidad
 * no es un lujo — es una necesidad.
 * <p>
 * Decidí manejar las llaves RSA como rutas de archivo en lugar de incrustar
 * el contenido directamente. Esto me da flexibilidad: en desarrollo apunto a
 * archivos locales generados por {@code bootstrap-swarm.sh}, y en producción
 * Docker Swarm monta esos mismos archivos como secretos. El gateway nunca
 * "ve" la diferencia.
 *
 * @param issuer        El emisor del token — normalmente la URL del auth-service.
 * @param expiration    Duración de validez del token. La puse como {@link Duration}
 *                      para evitar confusiones con milisegundos, segundos, etc.
 * @param rsa           Par de llaves RSA para firmar y verificar los tokens.
 * @author Alexander Rubio Cáceres
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
        String issuer,
        Duration expiration,
        RsaKeys rsa
) {
    /**
     * Rutas a las llaves RSA.
     * <p>
     * Separé publicKeyPath y privateKeyPath porque aunque el gateway solo
     * necesita la llave pública para verificar tokens, el auth-service necesita
     * la privada para firmarlos. Al tener ambas rutas aquí, centralizo la
     * configuración de llaves sin duplicar propiedades en dos servicios.
     *
     * @param publicKeyPath  Ruta al archivo PEM con la llave pública.
     * @param privateKeyPath Ruta al archivo PEM con la llave privada.
     */
    public record RsaKeys(
            String publicKeyPath,
            String privateKeyPath
    ) {}
}
