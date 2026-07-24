package com.peribook.bff.infrastructure.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Cliente reactivo para el servicio de publicaciones (post-service).
 * <p>
 * Este es el cliente que inicia toda la cadena del feed: sin publicaciones
 * no hay nada que enriquecer. Decidi separarlo de los otros clientes porque
 * cada uno tiene su propia URL base, semantica de errores y contrato de API.
 * Mezclarlos en una sola clase habria creado una dependencia oculta entre
 * servicios que no tienen relacion directa.
 * </p>
 * <p>
 * La respuesta se modela como {@code List<Map<String, Object>>} en vez de un
 * DTO tipado porque el BFF no necesita conocer la estructura completa de una
 * publicacion — solo extrae 4 campos (id, autorId, contenido, creadaEn). Un
 * DTO intermedio agregaria complejidad sin beneficio real en este caso.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@Component
public class PostClient {

    private static final Logger log = LoggerFactory.getLogger(PostClient.class);
    private final WebClient webClient;

    public PostClient(WebClient.Builder webClientBuilder) {
        // post-service corre en el puerto 8083 dentro del stack de Docker Swarm.
        this.webClient = webClientBuilder.baseUrl("http://post-service:8083").build();
    }

    /**
     * Recupera las ultimas publicaciones del servicio de posts.
     *
     * @param limite      cantidad maxima de publicaciones a solicitar
     * @param bearerToken token JWT que se propaga a post-service para autorizacion
     * @return lista de mapas con los datos crudos de cada publicacion
     */
    @SuppressWarnings("unchecked")
    public Mono<List<Map<String, Object>>> listarPublicaciones(int limite, String bearerToken) {
        return webClient.get()
                // Construyo la URI con query param "limite" para que el backend pueda
                // paginar desde el origen, en vez de traer todo y filtrar aca.
                .uri(uriBuilder -> uriBuilder.path("/api/posts").queryParam("limite", limite).build())
                .header("Authorization", bearerToken)
                .retrieve()
                .bodyToMono(List.class)
                .map(list -> (List<Map<String, Object>>) list)
                .doOnNext(posts -> log.debug("post-service: {} publicaciones obtenidas", posts.size()))
                .doOnError(e -> log.error("Error al llamar a post-service", e));
    }
}
