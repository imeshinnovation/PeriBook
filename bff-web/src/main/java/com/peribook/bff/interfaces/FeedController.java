package com.peribook.bff.interfaces;

import com.peribook.bff.application.ObtenerFeedEnriquecidoUseCase;
import com.peribook.bff.domain.FeedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * Controlador REST del BFF que expone el endpoint de feed enriquecido.
 * <p>
 * Decidi mantener este controlador extremadamente delgado: solo recibe la
 * request, delega en el caso de uso, y devuelve el resultado. No hay logica
 * de negocio aqui. Esto sigue el principio de separacion de capas de Clean
 * Architecture: la interfaz (el controlador) solo maneja HTTP, no decide
 * como construir el feed.
 * </p>
 * <p>
 * El uso de WebFlux (Flux como tipo de retorno) permite streaming reactivo:
 * a medida que el caso de uso va enriqueciendo publicaciones, estas se envian
 * al cliente sin esperar a que todas esten listas.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@RestController
@RequestMapping("/bff")
public class FeedController {

    private static final Logger log = LoggerFactory.getLogger(FeedController.class);
    private final ObtenerFeedEnriquecidoUseCase useCase;

    public FeedController(ObtenerFeedEnriquecidoUseCase useCase) {
        this.useCase = useCase;
    }

    /**
     * Obtiene el feed enriquecido del usuario autenticado.
     * <p>
     * El parametro {@code limite} tiene un valor por defecto de 20 y se limita
     * a 50 como maximo para evitar que un cliente pida 10.000 publicaciones de
     * una sola vez. La limitacion se aplica del lado del servidor porque confiar
     * solo en el cliente para esto no es seguro.
     * </p>
     *
     * @param limite    maximo de items a retornar (default 20, maximo 50)
     * @param authHeader token JWT en el header Authorization
     * @return flujo reactivo de items del feed
     */
    @GetMapping("/feed")
    public Flux<FeedItem> obtenerFeed(@RequestParam(defaultValue = "20") int limite,
                                       @RequestHeader("Authorization") String authHeader) {
        log.info("GET /bff/feed (limite={})", limite);
        // Math.min(limite, 50) es un safeguard para que el cliente no pueda
        // saturar el BFF pidiendo tamanos de pagina excesivos.
        return useCase.ejecutar(Math.min(limite, 50), authHeader);
    }
}
