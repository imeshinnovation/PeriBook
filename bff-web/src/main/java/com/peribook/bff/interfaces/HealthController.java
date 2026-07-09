package com.peribook.bff.interfaces;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Smoke test endpoint — indica que bff-web está vivo.
 * Se reemplazará con endpoints reales en la Fase 6.
 */
@RestController
public class HealthController {

    @GetMapping("/")
    public Mono<Map<String, String>> root() {
        return Mono.just(Map.of(
                "service", "bff-web",
                "status", "running",
                "fase", "2 — esqueleto"
        ));
    }
}
<!-- 2026-07-09 -->
