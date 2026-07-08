package com.peribook.bff;

import com.peribook.bff.application.ObtenerFeedEnriquecidoUseCase;
import com.peribook.bff.infrastructure.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@Import(SecurityConfig.class)
class BffSecurityTest {

    @MockBean
    private ObtenerFeedEnriquecidoUseCase useCase;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("GET /bff/feed sin token debe devolver 401")
    void feedRequiereJwt() {
        webTestClient.get()
                .uri("/bff/feed")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
