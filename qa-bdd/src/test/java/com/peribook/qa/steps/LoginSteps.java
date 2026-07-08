package com.peribook.qa.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    private Response response;

    @Dado("que el usuario {string} existe en el sistema")
    public void usuarioExiste(String email) {
        // Asume que el seeder de auth-service ya insertó los usuarios de prueba
    }

    @Cuando("intento iniciar sesión con email {string} y contraseña {string}")
    public void login(String email, String password) {
        response = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", password))
                .post("/api/auth/login");
    }

    @Entonces("el servicio responde con código {int}")
    public void verificarCodigo(int codigo) {
        assertThat(response.getStatusCode()).isEqualTo(codigo);
    }

    @Entonces("la respuesta contiene un token JWT válido")
    public void tokenJwtValido() {
        String token = response.jsonPath().getString("token");
        assertThat(token).isNotEmpty();
        // JWT tiene 3 partes separadas por punto
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Entonces("la respuesta contiene el alias {string}")
    public void verificarAlias(String alias) {
        assertThat(response.jsonPath().getString("alias")).isEqualTo(alias);
    }

    @Entonces("la respuesta contiene el userId del usuario")
    public void verificarUserId() {
        assertThat(response.jsonPath().getString("userId")).isNotEmpty();
    }

    @Entonces("el mensaje de error es {string}")
    public void mensajeError(String mensaje) {
        String detail = response.jsonPath().getString("detail");
        assertThat(detail).contains(mensaje);
    }
}
