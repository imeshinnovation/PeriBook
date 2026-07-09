package com.peribook.qa.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    @Dado("que el usuario {string} existe en el sistema")
    public void usuarioExiste(String email) {
        // Los usuarios de prueba los inserta el seeder al iniciar
    }

    @Cuando("intento iniciar sesión con email {string} y contraseña {string}")
    public void login(String email, String password) {
        Response response = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", password))
                .post("/api/auth/login");
        TestContext.setResponse(response);
        if (response.getStatusCode() == 200) {
            TestContext.setToken(response.jsonPath().getString("token"));
            TestContext.setUserId(response.jsonPath().getString("userId"));
        }
    }

    @Entonces("el servicio responde con código {int}")
    public void verificarCodigo(int codigo) {
        Response response = TestContext.getResponse();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(codigo);
    }

    @Entonces("la respuesta contiene un token JWT válido")
    public void tokenJwtValido() {
        String token = TestContext.getResponse().jsonPath().getString("token");
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Entonces("la respuesta contiene el alias {string}")
    public void verificarAlias(String alias) {
        assertThat(TestContext.getResponse().jsonPath().getString("alias")).isEqualTo(alias);
    }

    @Entonces("la respuesta contiene el userId del usuario")
    public void verificarUserId() {
        assertThat(TestContext.getResponse().jsonPath().getString("userId")).isNotEmpty();
    }

    @Entonces("el mensaje de error es {string}")
    public void mensajeError(String mensaje) {
        String detail = TestContext.getResponse().jsonPath().getString("detail");
        assertThat(detail).contains(mensaje);
    }
}
<!-- 2026-07-09 -->
