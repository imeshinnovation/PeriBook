package com.peribook.bff;

import com.peribook.bff.infrastructure.SecurityConfig;
import com.peribook.bff.interfaces.HealthController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = HealthController.class)
@Import(SecurityConfig.class)
class BffWebSmokeTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("GET / debe devolver 200 sin autenticación")
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
