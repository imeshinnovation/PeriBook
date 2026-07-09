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

                // ── Swagger / OpenAPI docs de cada servicio ──
                .route("auth-service-docs", r -> r
                        .path("/v3/api-docs/auth-service")
                        .filters(f -> f.rewritePath(
                                "/v3/api-docs/auth-service", "/v3/api-docs"))
                        .uri("http://auth-service:8081"))

                .route("user-service-docs", r -> r
                        .path("/v3/api-docs/user-service")
                        .filters(f -> f.rewritePath(
                                "/v3/api-docs/user-service", "/v3/api-docs"))
                        .uri("http://user-service:8082"))

                .route("post-service-docs", r -> r
                        .path("/v3/api-docs/post-service")
                        .filters(f -> f.rewritePath(
                                "/v3/api-docs/post-service", "/v3/api-docs"))
                        .uri("http://post-service:8083"))

                .route("like-service-docs", r -> r
                        .path("/v3/api-docs/like-service")
                        .filters(f -> f.rewritePath(
                                "/v3/api-docs/like-service", "/v3/api-docs"))
                        .uri("http://like-service:8084"))

                .route("bff-web-docs", r -> r
                        .path("/v3/api-docs/bff-web")
                        .filters(f -> f.rewritePath(
                                "/v3/api-docs/bff-web", "/v3/api-docs"))
                        .uri("http://bff-web:8086"))

                .build();
    }
}
