package com.peribook.auth.integration;

import com.peribook.auth.domain.Email;
import com.peribook.auth.domain.Password;
import com.peribook.auth.domain.Usuario;
import com.peribook.auth.domain.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("dev")
@org.junit.jupiter.api.Tag("integration")
class AuthIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("authdb")
            .withUsername("authuser")
            .withPassword("authpass");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        // Insertar un usuario de prueba directamente vía dominio
        if (usuarioRepository.findByEmail(new Email("test@peribook.com")).isEmpty()) {
            usuarioRepository.save(Usuario.registrar(
                    new Email("test@peribook.com"),
                    Password.fromRaw("test12345"),
                    "test_user"));
        }
    }

    @Test
    @DisplayName("POST /api/auth/login debe devolver JWT con credenciales correctas")
    void loginExitosoDebeDevolverJwt() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of(
                "email", "test@peribook.com",
                "password", "test12345");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        // Act
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login", request, Map.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("token")).asString().isNotEmpty();
        assertThat(response.getBody().get("userId")).asString().isNotEmpty();
        assertThat(response.getBody().get("alias")).isEqualTo("test_user");
    }

    @Test
    @DisplayName("POST /api/auth/login debe devolver 401 con credenciales incorrectas")
    void loginFallidoDebeDevolver401() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of(
                "email", "test@peribook.com",
                "password", "wrong-password");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        // Act
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login", request, Map.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("POST /api/auth/login debe devolver 400 con email inválido")
    void emailInvalidoDebeDevolver400() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of(
                "email", "no-es-un-email",
                "password", "test12345");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        // Act
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login", request, Map.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
<!-- 2026-07-09 -->
