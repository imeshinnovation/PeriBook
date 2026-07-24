package com.peribook.user.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Configuración de seguridad del microservicio user-service.
 * <p>
 * Este servicio confía en el auth-service para emitir JWTs — el user-service solo
 * necesita validar la firma del token usando la clave pública RSA. No gestiona
 * sesiones, no tiene login propio, no almacena credenciales. Es un diseño
 * stateless de extremo a extremo.
 * </p>
 * <p>
 * Decidí usar {@code SessionCreationPolicy.STATELESS} y deshabilitar CSRF porque
 * este es un servicio REST consumido por un BFF (Backend For Frontend) y no por
 * navegadores directamente. El BFF es quien maneja las sesiones web; el user-service
 * solo recibe tokens JWT en el header {@code Authorization}.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Cadena de filtros de seguridad HTTP.
     * Marcada con {@code @Order(Ordered.HIGHEST_PRECEDENCE)} para que se registre
     * antes que cualquier otro filtro — así aseguramos que el manejo de tokens
     * JWT y las rutas públicas (health check, Swagger) estén definidas al inicio.
     * <p>
     * Rutas públicas:
     * <ul>
     *   <li>{@code /actuator/health} — para el health check de Docker Swarm</li>
     *   <li>{@code /v3/api-docs/**}, {@code /swagger-ui/**} — documentación OpenAPI</li>
     * </ul>
     * Todo lo demás requiere un JWT válido.
     * </p>
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
                // Respuesta RFC 9457 (Problem Details) cuando falta el token, en lugar
                // del HTML por defecto de Spring Security. Esto es importante para un API
                // REST que se consume programáticamente.
                .authenticationEntryPoint((request, response, exception) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                    response.getWriter().write(
                            ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                                    "Token JWT requerido").toString());
                })
            );

        return http.build();
    }

    /**
     * Decodificador JWT basado en RSA.
     * Lee la clave pública desde la ruta configurada (primero el sistema de archivos
     * local para desarrollo, luego el classpath para el JAR empaquetado).
     * La clave se genera con {@code infra/bootstrap-swarm.sh} y se monta como
     * secreto de Docker Swarm en producción.
     * <p>
     * Elegí {@code NimbusJwtDecoder} porque es el que Spring Security recomienda
     * y soporta tanto RSA como HMAC. Además, ya viene como dependencia transitiva
     * de {@code spring-boot-starter-oauth2-resource-server}.
     * </p>
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(loadPem("keys/jwt-public.pem")));
            return NimbusJwtDecoder.withPublicKey(publicKey).build();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar la clave pública RSA", e);
        }
    }

    /**
     * Carga un archivo PEM (formato Base64 con cabeceras) desde disco o classpath.
     * Intenta primero la ruta absoluta/relativa al directorio de trabajo (útil en
     * desarrollo local con Docker Compose) y fallback al classpath (útil en el JAR
     * empaquetado de producción con secretos montados en {@code /run/secrets/}).
     * <p>
     * Este método extrae el contenido Base64 entre las cabeceras {@code -----BEGIN PUBLIC KEY-----}
     * y {@code -----END PUBLIC KEY-----}, elimina espacios y lo decodifica.
     * </p>
     *
     * @param path Ruta del archivo PEM
     * @return Arreglo de bytes con la clave decodificada
     */
    private byte[] loadPem(String path) throws IOException {
        // Intento primero en el sistema de archivos (desarrollo), luego classpath (JAR)
        Path filePath = Path.of(path);
        byte[] bytes = Files.exists(filePath) ? Files.readAllBytes(filePath)
                : new ClassPathResource(path).getInputStream().readAllBytes();
        String pem = new String(bytes);
        // Limpio las cabeceras y espacios para quedarme solo con el Base64
        String b64 = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
        return Base64.getDecoder().decode(b64);
    }
}
