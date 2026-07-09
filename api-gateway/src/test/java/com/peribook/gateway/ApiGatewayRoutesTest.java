package com.peribook.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class ApiGatewayRoutesTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("GET /actuator/health debe responder 200 sin autenticación")
    void healthEndpointDebeSerPublico() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("POST /api/auth/login no debe devolver 401 (ruta pública)")
    void loginEndpointDebeSerPublico() {
        webTestClient.post()
                .uri("/api/auth/login")
                .exchange()
                .expectStatus().value(status -> {
                    // No debe ser 401 (el endpoint es público en el Gateway)
                    assert status != 401 : "Login esperaba no 401, pero obtuvo " + status;
                });
    }

    @Test
    @DisplayName("GET /api/users/123 sin token debe devolver 401")
    void endpointProtegidoDebeRequerirToken() {
        webTestClient.get()
                .uri("/api/users/123")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
<!-- 2026-07-09 -->
