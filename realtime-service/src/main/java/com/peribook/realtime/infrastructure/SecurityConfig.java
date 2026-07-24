package com.peribook.realtime.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad HTTP para el servicio de tiempo real.
 * <p>
 * A diferencia de un servicio REST tradicional, aquí la seguridad se maneja
 * de forma muy distinta porque el tráfico WebSocket llega a través del API
 * Gateway (BFF), que ya valida el token JWT antes de enrutar la petición.
 * Por lo tanto, este servicio confía en que el Gateway ya filtró el tráfico
 * malicioso y no necesita re-validar el token.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@Configuration
public class SecurityConfig {

    /**
     * Cadena de filtros de seguridad para WebSocket y health check.
     * <p>
     * Uso {@code @Order(1)} para darle prioridad sobre cualquier otra cadena
     * que pudiera existir en el contexto. El {@code securityMatcher} restringe
     * esta cadena solo a las rutas {@code /ws/**} y {@code /actuator/health},
     * dejando otras rutas (si las hubiera) sin protección por ahora — pero como
     * {@code .anyRequest().denyAll()} cierra todo lo que no coincida, no hay
     * fugas.
     * </p>
     * <p>
     * <strong>Deshabilito CSRF</strong> porque WebSocket no es vulnerable a
     * ataques CSRF tradicionales: el handshake se hace sobre HTTP pero el
     * intercambio de mensajes posterior ocurre por un canal distinto (WebSocket
     * full-duplex). Además, el servicio es stateless, así que no hay sesión que
     * secuestrar.
     * </p>
     * <p>
     * <strong>Política de sesión STATELESS</strong> porque este servicio no
     * mantiene estado de sesión HTTP. Todas las decisiones de autenticación se
     * delegan al Gateway.
     * </p>
     * <p>
     * <strong>Actuator health</strong> se permite sin autenticación para que
     * Docker Swarm y los balanceadores puedan hacer health checks sin token.
     * </p>
     *
     * @param http el builder de seguridad HTTP de Spring Security
     * @return la cadena de filtros construida
     * @throws Exception si la configuración falla
     */
    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // WebSocket usa HTTP solo para el handshake inicial (SockJS);
            // luego cambia al protocolo WebSocket. El Gateway ya valida JWT
            // al enrutar /ws/**, así que aquí permitimos todo y delegamos la
            // autenticación al Gateway.
            .securityMatcher("/ws/**", "/actuator/health")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().denyAll()
            );
        return http.build();
    }
}
