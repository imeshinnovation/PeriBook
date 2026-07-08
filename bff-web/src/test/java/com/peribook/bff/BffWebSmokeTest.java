package com.peribook.bff;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@Import({com.peribook.bff.interfaces.HealthController.class,
         com.peribook.bff.infrastructure.SecurityConfig.class})
class BffWebSmokeTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("GET / debe devolver 200 con status running")
    void smokeTest() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.service").isEqualTo("bff-web")
                .jsonPath("$.status").isEqualTo("running");
    }
}
