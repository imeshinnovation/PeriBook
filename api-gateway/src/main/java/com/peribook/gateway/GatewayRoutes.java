package com.peribook.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GatewayRoutes {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ── Swagger UI (servido desde auth-service) ──
                .route("swagger-docs", r -> r
                        .path("/docs")
                        .filters(f -> f.rewritePath("/docs", "/swagger-ui/index.html"))
                        .uri("http://auth-service:8081"))

                .route("swagger-ui", r -> r
                        .path("/swagger-ui/**", "/webjars/**", "/v3/api-docs/**")
                        .uri("http://auth-service:8081"))

                // ── Microservicios ──────────────────────────
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("http://auth-service:8081"))

                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("http://user-service:8082"))

                .route("post-service", r -> r
                        .path("/api/posts/**")
                        .uri("http://post-service:8083"))

                .route("like-service", r -> r
                        .path("/api/likes/**")
                        .uri("http://like-service:8084"))

                .route("realtime-service", r -> r
                        .path("/ws/**")
                        .uri("http://realtime-service:8085"))

                .route("bff-web", r -> r
                        .path("/bff/**")
                        .uri("http://bff-web:8086"))

                .build();
    }
}
<!-- 2026-07-09 -->
