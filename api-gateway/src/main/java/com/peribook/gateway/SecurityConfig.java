package com.peribook.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtConfig jwtConfig;

    public SecurityConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(exchanges -> exchanges
                // Rutas públicas
                .pathMatchers("/api/auth/login").permitAll()
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers("/docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                // Todo lo demás requiere JWT
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
            );

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) java.security.KeyFactory.getInstance("RSA")
                    .generatePublic(new java.security.spec.X509EncodedKeySpec(
                            loadKeyBytes(jwtConfig.rsa().publicKeyPath())));
            return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar la clave pública RSA", e);
        }
    }

    private byte[] loadKeyBytes(String path) throws IOException {
        Path filePath = Path.of(path);
        byte[] bytes;
        if (Files.exists(filePath)) {
            log.info("Cargando clave desde sistema de archivos: {}", path);
            bytes = Files.readAllBytes(filePath);
        } else {
            log.info("Cargando clave desde classpath: {}", path);
            ClassPathResource resource = new ClassPathResource(path);
            try (InputStream in = resource.getInputStream()) {
                bytes = in.readAllBytes();
            }
        }
        // Parsear PEM: eliminar headers y decodificar Base64
        String pem = new String(bytes);
        String base64 = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        return java.util.Base64.getDecoder().decode(base64);
    }
}
