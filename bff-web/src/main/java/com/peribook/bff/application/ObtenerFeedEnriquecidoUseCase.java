package com.peribook.bff.application;

import com.peribook.bff.domain.FeedItem;
import com.peribook.bff.infrastructure.clients.LikeClient;
import com.peribook.bff.infrastructure.clients.PostClient;
import com.peribook.bff.infrastructure.clients.UserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class ObtenerFeedEnriquecidoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ObtenerFeedEnriquecidoUseCase.class);
    private final PostClient postClient;
    private final UserClient userClient;
    private final LikeClient likeClient;

    public ObtenerFeedEnriquecidoUseCase(PostClient postClient, UserClient userClient, LikeClient likeClient) {
        this.postClient = postClient;
        this.userClient = userClient;
        this.likeClient = likeClient;
    }

    /**
     * Orquesta en paralelo las llamadas a post-service, user-service y like-service
     * para componer un feed enriquecido en una sola respuesta.
     */
    public Flux<FeedItem> ejecutar(int limite) {
        log.info("Construyendo feed enriquecido (límite={})", limite);

        return postClient.listarPublicaciones(limite)
                .flatMapMany(Flux::fromIterable)
                .flatMap(post -> enriquecerPublicacion(post));
    }

    /**
     * Para cada publicación, obtiene en paralelo el alias del autor y el contador de likes.
     */
    private Mono<FeedItem> enriquecerPublicacion(Map<String, Object> post) {
        String publicacionId = (String) post.get("id");
        String autorId = (String) post.get("autorId");
        String contenido = (String) post.get("contenido");
        String creadaEn = post.get("creadaEn") != null ? post.get("creadaEn").toString() : "";

        Mono<String> aliasMono = userClient.obtenerPerfil(autorId)
                .map(perfil -> (String) perfil.getOrDefault("alias", "desconocido"))
                .onErrorResume(e -> {
                    log.warn("No se pudo obtener perfil de {}: {}", autorId, e.getMessage());
                    return Mono.just("desconocido");
                });

        Mono<Long> likesMono = likeClient.contarLikes(publicacionId);

        return Mono.zip(aliasMono, likesMono)
                .map(tuple -> new FeedItem(
                        publicacionId, autorId, contenido, creadaEn,
                        tuple.getT1(), tuple.getT2()));
    }
}
