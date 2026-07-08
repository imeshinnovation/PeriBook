package com.peribook.bff.infrastructure.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UserClient {

    private static final Logger log = LoggerFactory.getLogger(UserClient.class);
    private final WebClient webClient;

    public UserClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://user-service:8082").build();
    }

    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> obtenerPerfil(String userId) {
        return webClient.get()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> (Map<String, Object>) body)
                .doOnNext(perfil -> log.debug("user-service: perfil obtenido para {}", userId))
                .doOnError(e -> log.error("Error al llamar a user-service para {}", userId, e));
    }
}
