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

import java.util.Map;

/**
 * Caso de uso principal del BFF: construir el feed enriquecido que vera el usuario.
 * <p>
 * Decidi modelar esto como un caso de uso explícito (un solo @Component con un metodo
 * publico llamado {@code ejecutar}) en lugar de esparcir la logica en el controlador.
 * Esto sigue Clean Architecture: la capa de aplicacion orquesta, el controlador solo
 * recibe la request y delega. Si manana el feed necesita cache, paginacion con cursor,
 * o filtros, este es el unico lugar que cambia.
 * </p>
 * <p>
 * La estrategia reactiva es clave aca: cada publicacion del feed se enriquece con el
 * alias del autor y el contador de likes. Son tres llamadas HTTP (post-service,
 * user-service, like-service) que resuelvo de forma asincrona con {@code Mono.zip}.
 * Netty no bloquea hilos mientras esperamos, asi que el BFF escala con muy pocos
 * hilos del SO.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
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
     * Obtiene la lista de publicaciones desde post-service y las enriquece en paralelo.
     *
     * @param limite      maximo de publicaciones a traer
     * @param bearerToken token JWT del usuario autenticado (se propaga a los servicios internos)
     * @return flujo reactivo de ítems del feed enriquecido
     */
    public Flux<FeedItem> ejecutar(int limite, String bearerToken) {
        log.info("Construyendo feed enriquecido (limite={})", limite);

        return postClient.listarPublicaciones(limite, bearerToken)
                .flatMapMany(Flux::fromIterable)
                // Uso flatMapSequential en vez de flatMap porque asegura que los items
                // del feed se emitan en el mismo orden en que los devolvio post-service.
                // flatMap los procesa en paralelo pero no garantiza orden; flatMapSequential
                // si, y es importante para que el feed no llegue desordenado al frontend.
                .flatMapSequential(post -> enriquecerPublicacion(post, bearerToken));
    }

    /**
     * Enriquece una publicacion con datos de user-service y like-service.
     * <p>
     * Llama a ambos servicios en paralelo con {@code Mono.zip} y combina los resultados
     * en un {@link FeedItem}. Si user-service falla, el alias cae a "desconocido" en vez
     * de reventar el feed completo (tolerancia a fallos parciales).
     * </p>
     */
    private Mono<FeedItem> enriquecerPublicacion(Map<String, Object> post, String bearerToken) {
        String publicacionId = (String) post.get("id");
        String autorId = (String) post.get("autorId");
        String contenido = (String) post.get("contenido");
        String creadaEn = post.get("creadaEn") != null ? post.get("creadaEn").toString() : "";

        // Llamada a user-service: si falla, devolvemos "desconocido" en vez de
        // propagar el error. Esto evita que un perfil caido opaque todo el feed.
        Mono<String> aliasMono = userClient.obtenerPerfil(autorId, bearerToken)
                .map(perfil -> (String) perfil.getOrDefault("alias", "desconocido"))
                .onErrorResume(e -> {
                    log.warn("No se pudo obtener perfil de {}: {}", autorId, e.getMessage());
                    return Mono.just("desconocido");
                });

        // Llamada a like-service para obtener el contador de likes de la publicacion.
        Mono<Long> likesMono = likeClient.contarLikes(publicacionId, bearerToken);

        // Mono.zip ejecuta ambas llamadas en paralelo, combinando los resultados
        // en un solo FeedItem. Si like-service falla, el fallback esta dentro del
        // propio client (devuelve 0L), asi que no se necesita onErrorResume aca.
        return Mono.zip(aliasMono, likesMono)
                .map(tuple -> new FeedItem(
                        publicacionId, autorId, contenido, creadaEn,
                        tuple.getT1(), tuple.getT2()));
    }
}
