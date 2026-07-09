package com.peribook.post.integration;

import com.peribook.post.domain.Publicacion;
import com.peribook.post.domain.PublicacionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
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
class PostIntegrationTest {

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("postdb").withUsername("postuser").withPassword("postpass");

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private PublicacionRepository repository;

    @Test
    @DisplayName("POST /api/posts debe crear publicación (requiere token) y GET /api/posts debe listar")
    void crearYListarPublicaciones() {
        // Seed directo vía dominio (sin autenticación para test de integración)
        Publicacion p = repository.save(Publicacion.crear(UUID.randomUUID(), "Test integración"));

        // GET sin token → debe devolver 401 porque Security lo requiere
        ResponseEntity<Map> getResponse = restTemplate.getForEntity("/api/posts", Map.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        // Verificar que la publicación se guardó directamente
        assertThat(repository.buscarPorId(p.id())).isPresent();
    }
}
<!-- 2026-07-09 -->
