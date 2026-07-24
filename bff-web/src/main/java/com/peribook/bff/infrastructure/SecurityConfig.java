package com.peribook.bff.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Configuracion de seguridad para el BFF usando Spring Security Reactive y JWT.
 * 

 * Decidi usar OAuth2 Resource Server con JWTs en vez de sesiones porque el BFF
 * es stateless: no mantiene estado de autenticacion en memoria ni en Redis. Cada
 * request lleva su token JWT en el header Authorization, y el BFF lo valida con
 * la clave publica RSA antes de propagarlo a los servicios internos.
 * 
 * 

 * Deshabilito CSRF porque este BFF solo sirve APIs REST (no hay formularios HTML
 * que proteger) y el cliente es una SPA que envia tokens JWT, no cookies de sesion.
 * Tambien uso  para que Spring Security
 * no intente guardar el contexto de autenticacion en sesion HTTP — eso seria
 * incompatible con el modelo stateless reactivo.
 * 
 *
 * @author Alexander Rubio Caceres
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Cadena de filtros de seguridad WebFlux.
     * 

     * Los endpoints publicos son: raiz (health check) y Swagger/OpenAPI para
     * documentacion. Todo lo demas requiere un JWT valido.
     * 
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            // CSRF deshabilitado porque es una API REST consumida por una SPA.
            // Las APIs stateless con JWT no son vulnerables a CSRF.
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            // Sin repositorio de contexto de seguridad: no queremos sesiones HTTP.
            // Cada request se autentica de forma independiente.
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

            .authorizeExchange(exchanges -> exchanges
                // Health check y Swagger son accesibles sin autenticacion
                .pathMatchers("/", "/actuator/health").permitAll()
                .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/webjars/**").permitAll()
                // Cualquier otro endpoint requiere JWT valido
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
            );

        return http.build();
    }

    /**
     * Decodificador de JWT usando clave publica RSA (asimetrica).
     * 

     * Decidi usar RSA asimetrico en vez de HMAC simetrico porque la clave privada
     solo la tiene el servicio de autenticacion que emite los tokens. El BFF solo
     * necesita la clave publica para validar firmas, lo que reduce el riesgo si
     * alguien compromete este servicio: no podria emitir tokens falsos.
     * 
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        try {
            // Cargo la clave publica desde un archivo PEM. En desarrollo local
            // se lee del classpath; en produccion de un secreto de Docker Swarm.
            RSAPublicKey pk = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(loadPem("keys/jwt-public.pem")));
            return NimbusReactiveJwtDecoder.withPublicKey(pk).build();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar la clave publica RSA", e);
        }
    }

    /**
     * Lee un archivo PEM, extrae la seccion base64 y decodifica los bytes.
     * 

     * Soporta dos ubicaciones: primero busca en el sistema de archivos (para
     * secretos montados por Docker Swarm), y si no encuentra, carga desde el
     * classpath (para desarrollo local). Esto permite usar el mismo JAR en
     * ambos entornos sin recompilar.
     * 
     */
    private byte[] loadPem(String path) throws IOException {
        Path fp = Path.of(path);
        byte[] bytes = Files.exists(fp) ? Files.readAllBytes(fp)
                : new ClassPathResource(path).getInputStream().readAllBytes();
        // Elimino los encabezados PEM y los espacios en blanco, dejando solo
        // la cadena base64 que representa la clave.
        String b64 = new String(bytes)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(b64);
    }
}
