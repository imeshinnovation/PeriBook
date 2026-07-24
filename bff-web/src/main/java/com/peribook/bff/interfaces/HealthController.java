package com.peribook.bff.interfaces;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Endpoint de health check para el orquestador y monitoreo.
 * 

 * Este endpoint es necesario para que Docker Swarm pueda verificar que el
 * contenedor esta respondiendo (healthcheck en el stack) y para que el equipo
 * de operaciones confirme rapidamente que el servicio esta vivo despues de
 * un deploy.
 * 
 * 

 * A futuro, este endpoint puede extenderse para incluir verificaciones de
 * dependencias (post-service, user-service, like-service) y exponer metricas
 * de latencia. Por ahora devuelve un mapa simple con el estado basico.
 * 
 *
 * @author Alexander Rubio Caceres
 */
@RestController
public class HealthController {

    @GetMapping("/")
    public Mono<Map<String, String>> root() {
        return Mono.just(Map.of(
                "service", "bff-web",
                "status", "running"
        ));
    }
}
