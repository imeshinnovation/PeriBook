package com.peribook.auth.infrastructure.security;

import com.peribook.auth.application.JwtService;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;

/**
 * Implementación concreta de {@link JwtService} usando RS256 (RSA con SHA-256).
 * <p>
 * Elegí RS256 frente a HS256 (HMAC) porque RS256 usa un par de llaves
 * asimétricas: el auth-service firma con la llave privada y cualquier otro
 * servicio puede verificar con la llave pública sin compartir secretos. Esto
 * es fundamental en una arquitectura de microservicios porque los demás
 * servicios (post-service, user-service, etc.) pueden validar tokens sin
 * tener que llamar al auth-service ni compartir una clave secreta común.
 * </p>
 * <p>
 * Uso la librería {@code jjwt} (io.jsonwebtoken) porque es madura, activa y
 * tiene una API fluida que facilita la construcción de tokens. La uso solo
 * para emitir tokens; la verificación la delego en {@code NimbusJwtDecoder}
 * de Spring Security (ver {@link SecurityConfig#jwtDecoder()}).
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
@Service
public class RsaJwtService implements JwtService {

    private final JwtConfig jwtConfig;
    private final PrivateKey privateKey;

    /**
     * Recibe tanto la configuración como la llave privada ya cargada por
     * {@link SecurityConfig#privateKey()}. Prefiero inyectar la llave como
     * bean en vez de leer el archivo aquí mismo porque así la lógica de
     * carga (classpath vs. filesystem) está centralizada en un solo lugar.
     */
    public RsaJwtService(JwtConfig jwtConfig, PrivateKey privateKey) {
        this.jwtConfig = jwtConfig;
        this.privateKey = privateKey;
    }

    /**
     * Genera un token JWT compacto firmado con RS256.
     * <p>
     * El token incluye:
     * <ul>
     *   <li>{@code iss} — el emisor configurado (ej: "auth-service")</li>
     *   <li>{@code sub} — el email del usuario</li>
     *   <li>{@code userId} — claim personalizado con el UUID del usuario</li>
     *   <li>{@code iat} — fecha/hora de emisión</li>
     *   <li>{@code exp} — fecha/hora de expiración</li>
     * </ul>
     * </p>
     * <p>
     * Uso {@link Jwts#builder()} con {@code signWith} pasando la clave privada
     * y el algoritmo RS256. El token se firma en el momento de emisión y
     * cualquier servicio con la clave pública puede verificar su integridad.
     * </p>
     *
     * @param userId ID del usuario (UUID como string)
     * @param email  email del usuario (va como subject del token)
     * @return token JWT compacto en formato Base64URL
     */
    @Override
    public String generate(String userId, String email) {
        Instant now = Instant.now();
        // Calculo la expiración sumando la duración configurada al instante actual.
        // En producción suelo usar 1 hora, aunque en desarrollo uso 24h para no
        // tener que renovar tokens constantemente durante las pruebas.
        Instant expiration = now.plus(jwtConfig.expiration());

        return Jwts.builder()
                .issuer(jwtConfig.issuer())
                .subject(email)
                .claim("userId", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                // RS256: firma asimétrica. El token se verifica con la llave pública.
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }
}
