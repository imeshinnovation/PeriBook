package com.peribook.like.infrastructure.security;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Configuracion de seguridad del microservicio Like.
 * <p>
 * Configure el servicio como un resource server OAuth2 con autenticacion
 * stateless via JWT. Decidi exponer {@code /actuator/health} y la doc de
 * Swagger sin autenticacion porque son endpoints operativos que las
 * herramientas de monitoreo y los desarrolladores necesitan consumir sin
 * un token valido.
 * </p>
 * <p>
 * La clave publica RSA se carga desde {@code keys/jwt-public.pem}. Prefiero
 * cargarla desde un archivo PEM plano (en lugar de un keystore PKCS12) porque
 * es mas simple de generar y rotar con el script de bootstrap del swarm.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@Configuration
public class SecurityConfig {

    /**
     * Cadena de filtros de seguridad con prioridad mas alta.
     * <p>
     * Use {@code @Order(Ordered.HIGHEST_PRECEDENCE)} para asegurarme de que
     * este filtro se ejecute antes que cualquier otro filtro de la aplicacion.
     * </p>
     * <ul>
     *   <li>CSRF desactivado: el servicio no mantiene sesiones de usuario ni cookies.</li>
     *   <li>Politica de sesion STATELESS: cada request lleva su propio token JWT.</li>
     *   <li>Rutas publicas: health check y Swagger.</li>
     *   <li>Autenticacion JWT en resource server con decoder personalizado.</li>
     *   <li>Entry point personalizado que responde con ProblemDetail (RFC 9457).</li>
     * </ul>
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
                // Respuesta RFC 9457 Problem Detail en lugar del HTML por defecto
                .authenticationEntryPoint((request, response, exception) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                    response.getWriter().write(ProblemDetail.forStatusAndDetail(
                            HttpStatus.UNAUTHORIZED, "Token JWT requerido").toString());
                }));
        return http.build();
    }

    /**
     * Decodificador JWT configurado con la clave publica RSA.
     * <p>
     * Uso NimbusJwtDecoder (el default de Spring Security) con una clave RSA
     * en formato X.509. La clave se carga desde el classpath o desde el
     * sistema de archivos (para entornos Dockerizados donde el secreto se
     * monta como volumen).
     * </p>
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            RSAPublicKey pk = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(loadPem("keys/jwt-public.pem")));
            return NimbusJwtDecoder.withPublicKey(pk).build();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar la clave publica RSA", e);
        }
    }

    /**
     * Lee un archivo PEM, extrae la porcion Base64 entre los marcadores
     * BEGIN / END y la decodifica a bytes.
     * <p>
     * Soporta tanto el classpath (para desarrollo local) como el sistema de
     * archivos (para Docker Swarm con secretos montados). El metodo primero
     * busca en FileSystem y, si no encuentra, cae al ClassPathResource.
     * </p>
     */
    private byte[] loadPem(String path) throws IOException {
        Path fp = Path.of(path);
        byte[] bytes = Files.exists(fp)
                ? Files.readAllBytes(fp)
                : new ClassPathResource(path).getInputStream().readAllBytes();
        String b64 = new String(bytes)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(b64);
    }
}
