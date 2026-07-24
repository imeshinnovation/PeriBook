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

/**
 * Configuracion de seguridad del API Gateway.
 * <p>
 * Esta es, sin duda, la clase mas delicada del gateway. Aqui decidi que
 * toda la seguridad perimetral se maneje centralizadamente: el gateway valida
 * los tokens JWT de cada peticion entrante y, si son validos, reenvia la
 * solicitud al microservicio correspondiente. Los microservicios internos
 * confian en el gateway y no necesitan repetir esta validacion (aunque si un
 * microservicio se expusiera accidentalmente al exterior, no estaria
 * protegido -- por eso todos los servicios internos solo escuchan en la red
 * overlay de Swarm, no en puertos publicos).
 * <p>
 * Use {@code @EnableWebFluxSecurity} porque Spring Cloud Gateway corre sobre
 * WebFlux (reactivo), no sobre Spring MVC. Esto es importante: si usara las
 * anotaciones de seguridad de Spring MVC, simplemente no funcionarian porque
 * el motor subyacente es Netty, no Tomcat.
 * <p>
 * La autenticacion es stateless a proposito: no hay sesion HTTP, no hay
 * contexto de seguridad persistente. Cada request lleva su token y se valida
 * de forma independiente. Esto escala horizontalmente sin necesidad de un
 * session store compartido.
 *
 * @author Alexander Rubio Caceres
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtConfig jwtConfig;

    /**
     * Constructor con inyeccion de dependencias.
     * <p>
     * Recibo {@link JwtConfig} directamente -- Spring la construye a partir
     * de las propiedades {@code jwt.*} en application.yml gracias a
     * {@code @EnableConfigurationProperties}. Decidi inyectarla por constructor
     * (no por campo) porque es una buena practica: hace las dependencias
     * explicitas, facilita el testing y permite que el compilador verifique
     * que todo lo necesario esta presente.
     */
    public SecurityConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    /**
     * Cadena de filtros de seguridad WebFlux.
     * <p>
     * Aqui es donde defino que rutas son publicas y cuales requieren
     * autenticacion. La configuracion es intencionalmente restrictiva:
     * por defecto, todo requiere JWT valido. Solo exceptions explicitas
     * (login, health, Swagger, WebSocket) se marcan como {@code permitAll}.
     * <p>
     * Deshabilite CSRF porque el gateway no maneja sesiones basadas en
     * cookies -- los clientes se autentican via token JWT en el header
     * {@code Authorization: Bearer ...}. CSRF no aplica en este esquema.
     * <p>
     * Use {@link NoOpServerSecurityContextRepository} para asegurarme de que
     * Spring Security no intente almacenar el contexto de autenticacion entre
     * requests. Esto refuerza la naturaleza stateless del gateway y evita
     * fugas de memoria en un despliegue con muchas conexiones concurrentes.
     * <p>
     * Las rutas publicas son:
     * <ul>
     *   <li>{@code /api/auth/login} -- el login debe ser accesible sin token</li>
     *   <li>{@code /actuator/health} -- los health checks de Swarm</li>
     *   <li>{@code /docs/**}, {@code /swagger-ui/**}, {@code /v3/api-docs/**},
     *       {@code /webjars/**} -- documentacion</li>
     *   <li>{@code /ws/**} -- WebSocket handshake; la autenticacion se delega
     *       al servicio de realtime porque el protocolo WebSocket maneja la
     *       conexion de forma distinta</li>
     * </ul>
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(exchanges -> exchanges
                // Rutas publicas -- no requieren token
                .pathMatchers("/api/auth/login").permitAll()
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers("/docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                // WebSocket handshake: dejo pasar la conexion inicial y
                // dejo que el realtime-service valide el token por su cuenta
                .pathMatchers("/ws/**").permitAll()
                // Todo lo demas requiere JWT valido
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
            );

        return http.build();
    }

    /**
     * Decodificador reactivo de JWT usando RSA.
     * <p>
     * Cargo la llave publica RSA desde la ruta configurada en
     * {@code jwt.rsa.public-key-path} y construyo un
     * {@link NimbusReactiveJwtDecoder} con ella. Este decoder verifica que:
     * <ul>
     *   <li>La firma del token coincide con la llave publica</li>
     *   <li>El token no ha expirado</li>
     *   <li>El issuer es el esperado (opcional, segun configuracion)</li>
     * </ul>
     * <p>
     * Elegi RSA asimetrica (no HMAC) porque permite que cualquier servicio
     * verifique tokens con solo la llave publica, mientras que solo el
     * auth-service tiene la privada para firmarlos. Si usara HMAC, todos los
     * servicios tendrian que conocer la misma clave secreta -- un riesgo
     * innecesario.
     *
     * @throws IllegalStateException si no se puede cargar la llave publica
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) java.security.KeyFactory.getInstance("RSA")
                    .generatePublic(new java.security.spec.X509EncodedKeySpec(
                            loadKeyBytes(jwtConfig.rsa().publicKeyPath())));
            return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar la clave publica RSA", e);
        }
    }

    /**
     * Carga los bytes de una llave RSA desde el sistema de archivos o el
     * classpath.
     * <p>
     * Implemente una logica de fallback: primero intento leer el archivo
     * desde la ruta absoluta en el sistema de archivos (util para entornos
     * donde los secretos se montan como archivos, como en Docker Swarm).
     * Si el archivo no existe, busco en el classpath (util en desarrollo
     * local o tests).
     * <p>
     * El metodo tambien parsea el formato PEM: elimina los headers
     * ({@code -----BEGIN PUBLIC KEY-----}, etc.) y los saltos de linea,
     * dejando solo la cadena Base64 que luego decodifica a bytes.
     * Prefiero este parsing manual a usar una libreria externa de PEM
     * porque evito una dependencia mas para algo que es trivial de hacer
     * con la JDK estandar.
     *
     * @param path Ruta al archivo PEM (absoluta o relativa al classpath)
     * @return Bytes decodificados de la llave
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
        // Parseo manual de PEM: remuevo headers y decodifico Base64
        // Decidi no usar Bouncy Castle ni PemReader para mantener las
        // dependencias al minimo. El formato PEM no es mas que Base64
        // con un header y un footer, asi que un par de replaces bastan.
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
