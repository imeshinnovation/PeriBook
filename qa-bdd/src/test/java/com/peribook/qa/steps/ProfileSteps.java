package com.peribook.qa.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileSteps {

    private Response response;
    private String token;
    private String userId;

    @Cuando("consulto mi perfil")
    public void consultarPerfilPropio() {
        if (token == null) {
            // Autenticar primero
            Response login = SerenityRest.given()
                    .contentType("application/json")
                    .body(Map.of("email", "ana@peribook.com", "password", "secreto123"))
                    .post("/api/auth/login");
            token = login.jsonPath().getString("token");
            userId = login.jsonPath().getString("userId");
        }

        response = SerenityRest.given()
                .header("Authorization", "Bearer " + token)
                .get("/api/users/" + userId);
    }

    @Cuando("consulto el perfil con ID {string}")
    public void consultarPerfil(String id) {
        // Obtener token primero
        Response login = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", "ana@peribook.com", "password", "secreto123"))
                .post("/api/auth/login");
        token = login.jsonPath().getString("token");

        response = SerenityRest.given()
                .header("Authorization", "Bearer " + token)
                .get("/api/users/" + id);
    }

    @Entonces("el alias es {string}")
    public void verificarAlias(String alias) {
        assertThat(response.jsonPath().getString("alias")).isEqualTo(alias);
    }

    @Entonces("los nombres y apellidos están presentes")
    public void verificarNombres() {
        assertThat(response.jsonPath().getString("nombres")).isNotEmpty();
        assertThat(response.jsonPath().getString("apellidos")).isNotEmpty();
    }
}
