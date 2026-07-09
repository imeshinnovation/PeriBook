package com.peribook.qa.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.rest.SerenityRest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FeedSteps {

    private final TestContext ctx;

    public FeedSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Dado("que estoy autenticado como {string}")
    public void autenticar(String email) {
        ctx.response = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", "secreto123"))
                .post("/api/auth/login");
        ctx.token = ctx.response.jsonPath().getString("token");
        ctx.userId = ctx.response.jsonPath().getString("userId");
    }

    @Cuando("consulto el feed con límite {int}")
    public void consultarFeed(int limite) {
        ctx.response = SerenityRest.given()
                .header("Authorization", "Bearer " + ctx.token)
                .get("/bff/feed?limite=" + limite);
    }

    @Cuando("consulto el feed sin token")
    public void consultarFeedSinToken() {
        ctx.response = SerenityRest.given().get("/bff/feed");
    }

    @Entonces("la respuesta es un array de publicaciones")
    public void respuestaEsArray() {
        List<?> items = ctx.response.jsonPath().getList("$");
        assertThat(items).isNotNull();
    }

    @Entonces("cada publicación tiene publicacionId, contenido, autorAlias y totalLikes")
    public void cadaItemTieneCampos() {
        List<Map<String, Object>> items = ctx.response.jsonPath().getList("$");
        if (!items.isEmpty()) {
            Map<String, Object> first = items.get(0);
            assertThat(first).containsKeys("publicacionId", "contenido", "autorAlias", "totalLikes");
        }
    }

    @Cuando("creo una publicación con contenido {string}")
    public void crearPublicacion(String contenido) {
        ctx.response = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + ctx.token)
                .body(Map.of("contenido", contenido))
                .post("/api/posts");
        if (ctx.response.getStatusCode() == 201) {
            ctx.publicacionId = ctx.response.jsonPath().getString("id");
        }
    }

    @Cuando("creo una publicación con {int} caracteres")
    public void crearPublicacionLarga(int cantidad) {
        String largo = "a".repeat(cantidad);
        ctx.response = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + ctx.token)
                .body(Map.of("contenido", largo))
                .post("/api/posts");
    }

    @Entonces("la respuesta contiene el contenido {string}")
    public void verificarContenido(String contenido) {
        assertThat(ctx.response.jsonPath().getString("contenido")).isEqualTo(contenido);
    }

    @Entonces("la respuesta contiene un publicacionId")
    public void verificarPublicacionId() {
        assertThat(ctx.response.jsonPath().getString("id")).isNotEmpty();
    }
}
