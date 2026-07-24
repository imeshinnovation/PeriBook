package com.peribook.bff.infrastructure.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Cliente reactivo para el servicio de usuarios (user-service).
 * <p>
 * Use-service expone perfiles de usuario. El BFF solo necesita el alias
 * para mostrarlo en el feed — no el email, fecha de registro ni otros datos
 * sensibles. Decidi que este cliente devuelva el Map completo y que el caso
 * de uso extraiga solo "alias", porque asi si manana el feed necesita tambien
 * el avatar del usuario, no hay que cambiar el cliente, solo el caso de uso.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@Component
public class UserClient {

    private static final Logger log = LoggerFactory.getLogger(UserClient.class);
    private final WebClient webClient;

    public UserClient(WebClient.Builder webClientBuilder) {
        // user-service corre en el puerto 8082 dentro del stack de Docker Swarm.
        this.webClient = webClientBuilder.baseUrl("http://user-service:8082").build();
    }

    /**
     * Obtiene el perfil de un usuario por su ID.
     *
     * @param userId      identificador del usuario
     * @param bearerToken token JWT que se propaga para autorizacion
     * @return mapa con los campos del perfil
     */
    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> obtenerPerfil(String userId, String bearerToken) {
        return webClient.get()
                .uri("/api/users/{id}", userId)
                .header("Authorization", bearerToken)
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> (Map<String, Object>) body)
                .doOnNext(perfil -> log.debug("user-service: perfil obtenido para {}", userId))
                .doOnError(e -> log.error("Error al llamar a user-service para {}", userId, e));
    }
}
