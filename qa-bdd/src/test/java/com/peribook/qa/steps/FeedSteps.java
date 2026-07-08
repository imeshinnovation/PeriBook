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

    private Response response;
    private String token;

    @Dado("que estoy autenticado como {string}")
    public void autenticar(String email) {
        // Login para obtener token
        Response loginResponse = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", "secreto123"))
                .post("/api/auth/login");

        token = loginResponse.jsonPath().getString("token");
    }

    @Cuando("consulto el feed con límite {int}")
    public void consultarFeed(int limite) {
        response = SerenityRest.given()
                .header("Authorization", "Bearer " + token)
                .get("/bff/feed?limite=" + limite);
    }

    @Cuando("consulto el feed sin token")
    public void consultarFeedSinToken() {
        response = SerenityRest.given().get("/bff/feed");
    }

    @Entonces("la respuesta es un array de publicaciones")
    public void respuestaEsArray() {
        List<?> items = response.jsonPath().getList("$");
        assertThat(items).isNotNull();
    }

    @Entonces("cada publicación tiene publicacionId, contenido, autorAlias y totalLikes")
    public void cadaItemTieneCampos() {
        List<Map<String, Object>> items = response.jsonPath().getList("$");
        if (!items.isEmpty()) {
            Map<String, Object> first = items.get(0);
            assertThat(first).containsKeys("publicacionId", "contenido", "autorAlias", "totalLikes");
        }
    }

    @Cuando("creo una publicación con contenido {string}")
    public void crearPublicacion(String contenido) {
        response = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(Map.of("contenido", contenido))
                .post("/api/posts");
    }

    @Cuando("creo una publicación con {int} caracteres")
    public void crearPublicacionLarga(int cantidad) {
        String largo = "a".repeat(cantidad);
        response = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(Map.of("contenido", largo))
                .post("/api/posts");
    }

    @Entonces("la respuesta contiene el contenido {string}")
    public void verificarContenido(String contenido) {
        assertThat(response.jsonPath().getString("contenido")).isEqualTo(contenido);
    }

    @Entonces("la respuesta contiene un publicacionId")
    public void verificarPublicacionId() {
        assertThat(response.jsonPath().getString("id")).isNotEmpty();
    }
}
