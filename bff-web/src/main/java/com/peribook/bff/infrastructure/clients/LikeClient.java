package com.peribook.bff.infrastructure.clients;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.TimeoutException;

@Component
public class LikeClient {

    private static final Logger log = LoggerFactory.getLogger(LikeClient.class);
    private final WebClient webClient;

    public LikeClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://like-service:8084").build();
    }

    @CircuitBreaker(name = "likeService", fallbackMethod = "contarLikesFallback")
    @SuppressWarnings("unchecked")
    public Mono<Long> contarLikes(String publicacionId, String bearerToken) {
        return webClient.get()
                .uri("/api/likes/{publicacionId}/count", publicacionId)
                .header("Authorization", bearerToken)
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> {
                    Object count = body.get("total");
                    return count instanceof Number n ? n.longValue() : 0L;
                })
                .doOnError(e -> log.warn("Error al llamar a like-service para {}", publicacionId, e));
    }

    /**
     * Fallback: si like-service no responde, devuelve 0 — el feed no debe romperse.
     */
    private Mono<Long> contarLikesFallback(String publicacionId, Throwable t) {
        log.warn("Fallback like-service para publicacion {}: {}", publicacionId, t.getMessage());
        return Mono.just(0L);
    }
}
