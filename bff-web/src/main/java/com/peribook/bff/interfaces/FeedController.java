package com.peribook.bff.interfaces;

import com.peribook.bff.application.ObtenerFeedEnriquecidoUseCase;
import com.peribook.bff.domain.FeedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/bff")
public class FeedController {

    private static final Logger log = LoggerFactory.getLogger(FeedController.class);
    private final ObtenerFeedEnriquecidoUseCase useCase;

    public FeedController(ObtenerFeedEnriquecidoUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/feed")
    public Flux<FeedItem> obtenerFeed(@RequestParam(defaultValue = "20") int limite) {
        log.info("GET /bff/feed (límite={})", limite);
        return useCase.ejecutar(Math.min(limite, 50));
    }
}
