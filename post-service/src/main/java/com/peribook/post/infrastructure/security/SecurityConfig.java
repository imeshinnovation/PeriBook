package com.peribook.post.infrastructure.security;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Configuracion de seguridad del post-service.
 * 

 * Implemento autenticacion stateless mediante JSON Web Tokens (JWT) usando RSA.
 * El servicio actua como Resource Server de OAuth2: no emite tokens, solo los valida.
 * Los tokens son emitidos por el API Gateway (BFF) despues de que el usuario se
 * autentica. Cada microservicio valida el token por su cuenta usando la clave publica
 * compartida, lo que evita llamadas sincronas a un servicio de autenticacion central.
 * 

 * Decidi deshabilitar CSRF porque usamos autenticacion stateless basada en tokens.
 * En una API REST sin cookies de sesion, CSRF no tiene sentido. La session management
 * esta en modo STATELESS porque no queremos sesiones HTTP del lado del servidor.
 * 

 /> Los endpoints publicos son:
 * 
 *    * -  — para health checks del orquestador (Docker Swarm)
 *    * -  y  — para la documentacion OpenAPI
 * 
 * Todo lo demas requiere un token JWT valido.
 *
 * @author Alexander Rubio Caceres
 */
@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Cadena de filtros de seguridad de Spring Security.
     * 

     * Marque este bean con  para que se ejecute antes
     * que cualquier otro filtro. Esto es importante porque queremos que la autenticacion
     * ocurra lo antes posible en la cadena de filtros, antes de que cualquier otro
     * componente procese la peticion.
     * 

     * El punto de entrada de autenticacion usa ProblemDetail de Spring 6 para
     * devolver errores en formato RFC 7807 (Problem Details for HTTP APIs). Prefiero
     * este formato sobre un simple JSON porque es un estandar y facilita la integracion
     * con herramientas como Spring Cloud Gateway.
     *
     * @param http el builder de HttpSecurity configurado por Spring
     * @return el SecurityFilterChain construido
     * @throws Exception si ocurre algun error en la configuracion
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Sin estado, no necesitamos proteccion CSRF
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
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
     * Decodificador JWT que usa la clave publica RSA para validar los tokens.
     * 

     * La clave publica se carga desde  usando el metodo
     * #loadPem(String). Intenta primero desde el sistema de archivos (para
     * desarrollo local con Docker Compose) y si no encuentra el archivo, busca en
     * el classpath (para despliegues empaquetados en JAR).
     * 

     * Decidi usar RSA en lugar de HMAC porque con RSA solo el emisor necesita la
     * clave privada; los microservicios solo tienen la clave publica. Esto significa
     * que si un microservicio se ve comprometido, el atacante no puede emitir tokens
     * falsos porque no tiene la clave privada. Con HMAC todos los servicios tendrian
     * que compartir el mismo secreto, lo cual es mas riesgoso.
     *
     * @return un decodificador JWT configurado con la clave publica RSA
     * @throws IllegalStateException si no se puede cargar la clave publica
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(loadPem("keys/jwt-public.pem")));
            return NimbusJwtDecoder.withPublicKey(publicKey).build();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar la clave publica RSA", e);
        }
    }

    /**
     * Carga un archivo PEM, extrae la parte codificada en Base64 y la decodifica.
     * 

     * Soporta dos origenes: primero intenta leer desde el sistema de archivos
     * (para desarrollo) y si falla, desde el classpath (para produccion). Esto
     * permite que el mismo JAR funcione en ambos entornos.
     * 

     * Elimina los encabezados  y
     *  y cualquier espacio en blanco antes de
     * decodificar.
     *
     * @param path ruta del archivo PEM (relativa a la raiz del proyecto o classpath)
     * @return el arreglo de bytes decodificado
     * @throws IOException si no se puede leer el archivo
     */
    private byte[] loadPem(String path) throws IOException {
        Path filePath = Path.of(path);
        byte[] bytes = Files.exists(filePath) ? Files.readAllBytes(filePath)
                : new ClassPathResource(path).getInputStream().readAllBytes();
        String b64 = new String(bytes).replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
        return Base64.getDecoder().decode(b64);
    }
}
