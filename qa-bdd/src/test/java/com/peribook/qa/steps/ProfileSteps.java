package com.peribook.qa.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileSteps {

    private final TestContext ctx;

    public ProfileSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Cuando("consulto mi perfil")
    public void consultarPerfilPropio() {
        if (ctx.token == null) {
            autenticar("ana@peribook.com");
        }
        ctx.response = SerenityRest.given()
                .header("Authorization", "Bearer " + ctx.token)
                .get("/api/users/" + ctx.userId);
    }

    @Cuando("consulto el perfil con ID {string}")
    public void consultarPerfil(String id) {
        if (ctx.token == null) {
            autenticar("ana@peribook.com");
        }
        ctx.response = SerenityRest.given()
                .header("Authorization", "Bearer " + ctx.token)
                .get("/api/users/" + id);
    }

    @Entonces("el alias es {string}")
    public void verificarAlias(String alias) {
        assertThat(ctx.response.jsonPath().getString("alias")).isEqualTo(alias);
    }

    @Entonces("los nombres y apellidos están presentes")
    public void verificarNombres() {
        assertThat(ctx.response.jsonPath().getString("nombres")).isNotEmpty();
        assertThat(ctx.response.jsonPath().getString("apellidos")).isNotEmpty();
    }

    private void autenticar(String email) {
        ctx.response = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", "secreto123"))
                .post("/api/auth/login");
        ctx.token = ctx.response.jsonPath().getString("token");
        ctx.userId = ctx.response.jsonPath().getString("userId");
    }
}
