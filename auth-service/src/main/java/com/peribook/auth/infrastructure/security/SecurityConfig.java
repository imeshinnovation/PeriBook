package com.peribook.auth.infrastructure.security;

import com.peribook.auth.application.AutenticacionFallidaException;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Configuración de seguridad de Spring para el auth-service.
 * 

 * Esta es la clase más densa del servicio porque define toda la postura de
 * seguridad: qué endpoints son públicos, cuáles requieren autenticación,
 * cómo se validan los tokens JWT, cómo se cargan las llaves RSA, y cómo se
 * manejan los errores de autenticación.
 * 
 * 

 * Usé  en  para que
 * Spring no genere proxies CGLIB — esta clase solo define beans, no tiene
 * llamadas internas entre  que necesiten ser interceptadas.
 * Es una micro-optimización que evita overhead innecesario en el contexto
 * de la aplicación.
 * 
 * 

 * Decidí que el SecurityConfig#loadKeyBytes(String) haga el parsing
 * PEM manual en vez de usar una librería tipo Bouncy Castle porque:
 * 
 *    * - El formato PEM es trivial de parsear (base64 con headers)
 *    * - No quiero agregar una dependencia pesada solo para esto
 *    * - Bouncy Castle tiene problemas de tamaño y aprobación en algunos
 *       entornos corporativos por temas de export control
 * 
 * 
 *
 * @author Alexander Rubio Cáceres
 */
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtConfig jwtConfig;

    public SecurityConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    // ── Security filter chain ──────────────────────────
    // Esta es la cadena de filtros que Spring Security aplica a todas las
    // peticiones HTTP. La configuro como STATELESS porque los servicios no
    // deben mantener sesiones HTTP — cada request lleva su propio token JWT.

    /**
     * Define la cadena de filtros de seguridad HTTP.
     * 

     * Configuro:
     * 
     *    * - CSRF desactivado — somos una API REST sin estado
     *       que usa JWT, no cookies de sesión. CSRF no aplica aquí.
     *    * - Sin estado (STATELESS) — no se crean sesiones HTTP.
     *       Cada request se autentica de forma independiente con su JWT.
     *    * - Endpoints públicos — login, Swagger/OpenAPI y health
     *       check no requieren token. Todo lo demás sí.
     *    * - Resource Server OAuth2 — Spring Security valida el
     *       JWT automáticamente usando el decoder que configuro en
     *       #jwtDecoder().
     * 
     * 
     *
     * @param http el builder de HttpSecurity de Spring
     * @return la cadena de filtros construida
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desactivo CSRF porque no uso cookies de sesión.
            // Las APIs REST con JWT son inmunes a ataques CSRF por diseño.
            .csrf(csrf -> csrf.disable())

            // Política de sesión: STATELESS para no crear HttpSession.
            // Cada request debe incluir el token JWT en el header Authorization.
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configuración de autorización por endpoint.
            .authorizeHttpRequests(auth -> auth
                // Login es público porque es el punto de entrada para obtener el token.
                .requestMatchers("/api/auth/login").permitAll()
                // Documentación OpenAPI y Swagger UI son públicos.
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // Health check para orquestación (Docker Swarm, Kubernetes).
                .requestMatchers("/actuator/health").permitAll()
                // Cualquier otro endpoint requiere autenticación.
                .anyRequest().authenticated()
            )

            // Configuración como Resource Server OAuth2.
            // Spring Security valida el JWT automáticamente en cada request.
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
                // Entry point personalizado para errores JWT (token expirado, inválido, etc.)
                .authenticationEntryPoint((request, response, exception) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                    response.getWriter().write(
                            ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                                    "Token JWT requerido o inválido").toString());
                })
            )
            // Manejo de excepciones de autenticación general.
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, exception) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                    response.getWriter().write(
                            ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                                    "No autenticado").toString());
                })
            );

        return http.build();
    }

    // ── Beans JWT ───────────────────────────────────────
    // Estos beans se encargan de la criptografía: decodificador de tokens
    // para verificación, clave privada para firma, y codificador BCrypt.

    /**
     * Crea un JwtDecoder usando Nimbus (la implementación por defecto
     * de Spring Security) con la clave pública RSA.
     * 

     * Este decoder se usa tanto para verificar tokens entrantes como para que
     * Spring Security los valide automáticamente en cada request protegido.
     * 
     *
     * @return decoder de JWT configurado con la clave pública
     * @throws IllegalStateException si no se puede cargar la clave pública
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            // Cargo la clave pública y la uso para construir el decoder.
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new java.security.spec.X509EncodedKeySpec(
                            loadKeyBytes(jwtConfig.rsa().publicKeyPath())));
            return NimbusJwtDecoder.withPublicKey(publicKey).build();
        } catch (Exception e) {
            // Si la clave pública no se puede cargar, el servicio no puede
            // verificar tokens — mejor fallar en el arranque que en runtime.
            throw new IllegalStateException("No se pudo cargar la clave pública RSA", e);
        }
    }

    /**
     * Carga la clave privada RSA desde el archivo PEM para la firma de JWT.
     * 

     * La clave privada se inyecta como bean en RsaJwtService para
     * firmar los tokens emitidos durante el login.
     * 
     *
     * @return clave privada RSA lista para usar
     * @throws IllegalStateException si no se puede cargar la clave privada
     */
    @Bean
    public PrivateKey privateKey() {
        try {
            byte[] keyBytes = loadKeyBytes(jwtConfig.rsa().privateKeyPath());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar la clave privada RSA", e);
        }
    }

    /**
     * Bean de PasswordEncoder para hashear contraseñas con BCrypt.
     * 

     * Este bean lo usa Spring Security si se configura autenticación
     * basada en login form, pero en nuestro caso el hashing se maneja
     * directamente en el Value Object com.peribook.auth.domain.Password.
     * Lo defino aquí por si en el futuro necesito integrar autenticación
     * HTTP Basic o alguna otra funcionalidad que requiera un
     *  como bean de Spring.
     * 
     *
     * @return codificador BCrypt con fuerza por defecto (10 rounds)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── Helper ──────────────────────────────────────────

    /**
     * Carga un archivo de clave RSA en formato PEM y lo decodifica a bytes.
     * 

     * Estrategia de carga (por orden de prioridad):
     * 
     *    * - Sistema de archivos — para producción cuando las
     *       claves se montan como secretos de Docker Swarm en /run/secrets/
     *    * - Classpath — para desarrollo local cuando las claves
     *       están en src/main/resources/
     * 
     * 
     * 

     * Después de leer el archivo, remuevo los headers PEM
     * (-----BEGIN PUBLIC KEY-----, etc.) y decodifico la porción Base64
     * restante. Es un parsing sencillo pero suficiente.
     * 
     *
     * @param path ruta del archivo PEM (filesystem o classpath)
     * @return bytes decodificados de la clave (formato DER)
     * @throws IOException si no se puede leer el archivo de ninguna fuente
     */
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
