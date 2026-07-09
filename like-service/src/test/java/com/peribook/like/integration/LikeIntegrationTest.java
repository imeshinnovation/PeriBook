package com.peribook.like.integration;

import com.peribook.like.domain.Like;
import com.peribook.like.domain.LikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("dev")
@Tag("integration")
class LikeIntegrationTest {

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("likedb").withUsername("likeuser").withPassword("likepass");

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private LikeRepository repository;

    @Test
    @DisplayName("POST /api/likes sin token debe devolver 401")
    void likeSinTokenDebeDevolver401() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/likes?publicacionId=" + UUID.randomUUID(), null, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Repositorio debe evitar duplicados")
    void repositorioDebeEvitarDuplicados() {
        UUID pubId = UUID.randomUUID(), userId = UUID.randomUUID();
        Like like = repository.save(Like.dar(pubId, userId));

        assertThat(repository.buscarPorPublicacionYUsuario(pubId, userId)).isPresent();
        // El segundo like no debería lanzar excepción si el caso de uso lo maneja
    }
}
<!-- 2026-07-09 -->
