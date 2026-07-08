package com.peribook.bff.infrastructure.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class PostClient {

    private static final Logger log = LoggerFactory.getLogger(PostClient.class);
    private final WebClient webClient;

    public PostClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://post-service:8083").build();
    }

    @SuppressWarnings("unchecked")
    public Mono<List<Map<String, Object>>> listarPublicaciones(int limite) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/posts").queryParam("limite", limite).build())
                .retrieve()
                .bodyToMono(List.class)
                .map(list -> (List<Map<String, Object>>) list)
                .doOnNext(posts -> log.debug("post-service: {} publicaciones obtenidas", posts.size()))
                .doOnError(e -> log.error("Error al llamar a post-service", e));
    }
}
