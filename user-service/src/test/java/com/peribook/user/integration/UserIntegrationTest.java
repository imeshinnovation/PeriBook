package com.peribook.user.integration;

import com.peribook.user.domain.Email;
import com.peribook.user.domain.Perfil;
import com.peribook.user.domain.PerfilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("dev")
@Tag("integration")
class UserIntegrationTest {

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("userdb").withUsername("useruser").withPassword("userpass");

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private PerfilRepository perfilRepository;

    private UUID perfilId;

    @BeforeEach
    void setUp() {
        Perfil perfil = perfilRepository.save(Perfil.crear(
                UUID.randomUUID(), new Email("test@peribook.com"),
                "test", "Test", "User", LocalDate.of(2000, 1, 1)));
        perfilId = perfil.id();
    }

    @Test
    @DisplayName("GET /api/users/{id} debe devolver perfil cuando existe")
    void debeObtenerPerfil() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/users/" + perfilId, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("alias")).isEqualTo("test");
    }

    @Test
    @DisplayName("GET /api/users/{id} debe devolver 404 cuando no existe")
    void debeDevolver404() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/users/" + UUID.randomUUID(), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
<!-- 2026-07-09 -->
