package com.peribook.bff.infrastructure.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Cliente reactivo para el servicio de likes (like-service).
 * <p>
 * Decidi crear un cliente dedicado en lugar de usar WebClient directamente
 * en el caso de uso porque asi encapsulo la URL, el timeout y la logica de
 * parsing en un solo lugar. Si la API de like-service cambia, solo toco esta
 * clase; el caso de uso no se entera.
 * </p>
 * <p>
 * Este cliente aplica el patron de tolerancia a fallos parciales: si
 * like-service no responde, devuelve 0 likes en vez de lanzar una excepcion
 * que derribe el feed completo. Es mejor mostrar una publicacion sin contador
 * que no mostrarla.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@Component
public class LikeClient {

    private static final Logger log = LoggerFactory.getLogger(LikeClient.class);
    private final WebClient webClient;

    public LikeClient(WebClient.Builder webClientBuilder) {
        // La URL base apunta al alias de Docker Swarm "like-service" en el puerto 8084.
        // Dentro del stack todos los servicios se descubren por nombre de servicio.
        this.webClient = webClientBuilder.baseUrl("http://like-service:8084").build();
    }

    /**
     * Obtiene el total de likes de una publicacion.
     * <p>
     * El endpoint /api/likes/{publicacionId}/count devuelve un JSON con un campo
     * "total". Parseamos el Map y extraemos el valor numerico. Si la respuesta
     * no es la esperada o el servicio falla, retornamos 0 como fallback silencioso.
     * </p>
     */
    @SuppressWarnings("unchecked")
    public Mono<Long> contarLikes(String publicacionId, String bearerToken) {
        return webClient.get()
                .uri("/api/likes/{publicacionId}/count", publicacionId)
                .header("Authorization", bearerToken)
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> {
                    Object count = body.get("total");
                    // instanceof pattern matching de Java 21: evita cast explicito y
                    // maneja tanto Integer como Long en el JSON automaticamente.
                    return count instanceof Number n ? n.longValue() : 0L;
                })
                .doOnError(e -> log.warn("Error al llamar a like-service para {}: {}", publicacionId, e.getMessage()))
                // Fallback silencioso: si like-service caera, el feed sigue funcionando
                // sin el contador. Prefiero datos parciales a un error 500 para el usuario.
                .onErrorReturn(0L);
    }
}
