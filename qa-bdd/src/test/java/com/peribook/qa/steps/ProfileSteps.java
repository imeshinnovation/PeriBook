package com.peribook.qa.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileSteps {

    @Cuando("consulto mi perfil")
    public void consultarPerfilPropio() {
        if (TestContext.getToken() == null) {
            autenticar("ana@peribook.com");
        }
        Response response = SerenityRest.given()
                .header("Authorization", "Bearer " + TestContext.getToken())
                .get("/api/users/" + TestContext.getUserId());
        TestContext.setResponse(response);
    }

    @Cuando("consulto el perfil con ID {string}")
    public void consultarPerfil(String id) {
        if (TestContext.getToken() == null) {
            autenticar("ana@peribook.com");
        }
        Response response = SerenityRest.given()
                .header("Authorization", "Bearer " + TestContext.getToken())
                .get("/api/users/" + id);
        TestContext.setResponse(response);
    }

    @Entonces("el alias es {string}")
    public void verificarAlias(String alias) {
        assertThat(TestContext.getResponse().jsonPath().getString("alias")).isEqualTo(alias);
    }

    @Entonces("los nombres y apellidos están presentes")
    public void verificarNombres() {
        assertThat(TestContext.getResponse().jsonPath().getString("nombres")).isNotEmpty();
        assertThat(TestContext.getResponse().jsonPath().getString("apellidos")).isNotEmpty();
    }

    private void autenticar(String email) {
        Response response = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", "secreto123"))
                .post("/api/auth/login");
        TestContext.setToken(response.jsonPath().getString("token"));
        TestContext.setUserId(response.jsonPath().getString("userId"));
    }
}
<!-- 2026-07-09 -->
