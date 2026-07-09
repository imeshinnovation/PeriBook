package com.peribook.qa.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FeedSteps {

    @Dado("que estoy autenticado como {string}")
    public void autenticar(String email) {
        Response response = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", "secreto123"))
                .post("/api/auth/login");
        TestContext.setToken(response.jsonPath().getString("token"));
        TestContext.setUserId(response.jsonPath().getString("userId"));
    }

    @Cuando("consulto el feed con límite {int}")
    public void consultarFeed(int limite) {
        Response response = SerenityRest.given()
                .header("Authorization", "Bearer " + TestContext.getToken())
                .get("/bff/feed?limite=" + limite);
        TestContext.setResponse(response);
    }

    @Cuando("consulto el feed sin token")
    public void consultarFeedSinToken() {
        TestContext.setResponse(SerenityRest.given().get("/bff/feed"));
    }

    @Entonces("la respuesta es un array de publicaciones")
    public void respuestaEsArray() {
        List<?> items = TestContext.getResponse().jsonPath().getList("$");
        assertThat(items).isNotNull();
    }

    @Entonces("cada publicación tiene publicacionId, contenido, autorAlias y totalLikes")
    public void cadaItemTieneCampos() {
        List<Map<String, Object>> items = TestContext.getResponse().jsonPath().getList("$");
        if (!items.isEmpty()) {
            Map<String, Object> first = items.get(0);
            assertThat(first).containsKeys("publicacionId", "contenido", "autorAlias", "totalLikes");
        }
    }

    @Cuando("creo una publicación con contenido {string}")
    public void crearPublicacion(String contenido) {
        Response response = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + TestContext.getToken())
                .body(Map.of("contenido", contenido))
                .post("/api/posts");
        TestContext.setResponse(response);
        if (response.getStatusCode() == 201) {
            TestContext.setPublicacionId(response.jsonPath().getString("id"));
        }
    }

    @Cuando("creo una publicación con {int} caracteres")
    public void crearPublicacionLarga(int cantidad) {
        String largo = "a".repeat(cantidad);
        TestContext.setResponse(SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + TestContext.getToken())
                .body(Map.of("contenido", largo))
                .post("/api/posts"));
    }

    @Entonces("la respuesta contiene el contenido {string}")
    public void verificarContenido(String contenido) {
        assertThat(TestContext.getResponse().jsonPath().getString("contenido")).isEqualTo(contenido);
    }

    @Entonces("la respuesta contiene un publicacionId")
    public void verificarPublicacionId() {
        assertThat(TestContext.getResponse().jsonPath().getString("id")).isNotEmpty();
    }
}
<!-- 2026-07-09 -->
