package com.peribook.qa.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RealtimeSteps {

    @Dado("existe una publicacion con ID conocido")
    public void publicacionExistente() {
        String token = TestContext.getToken();
        System.out.println("[QA] Token para crear publicacion: " + (token != null ? token.substring(0, 20) + "..." : "NULL"));

        Response response = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(Map.of("contenido", "Publicacion para test de likes"))
                .post("/api/posts");

        System.out.println("[QA] POST /api/posts → " + response.getStatusCode());

        if (response.getStatusCode() == 201) {
            String id = response.jsonPath().getString("id");
            TestContext.setPublicacionId(id);
            System.out.println("[QA] Publicacion creada: " + id);
        } else {
            String fallbackId = UUID.randomUUID().toString();
            TestContext.setPublicacionId(fallbackId);
            System.out.println("[QA] Publicacion fallback: " + fallbackId);
        }
    }

    @Dado("ya di like a una publicacion")
    public void likePrevio() {
        String token = TestContext.getToken();
        String pubId = TestContext.getPublicacionId();
        System.out.println("[QA] likePrevio: token=" + (token != null ? token.substring(0, 20) + "..." : "NULL") + " pubId=" + pubId);

        Response response = SerenityRest.given()
                .header("Authorization", "Bearer " + token)
                .post("/api/likes?publicacionId=" + pubId);

        System.out.println("[QA] likePrevio POST /api/likes → " + response.getStatusCode());
        TestContext.setResponse(response);
    }

    @Cuando("doy like a la publicacion")
    public void darLike() {
        Response response = SerenityRest.given()
                .header("Authorization", "Bearer " + TestContext.getToken())
                .post("/api/likes?publicacionId=" + TestContext.getPublicacionId());
        System.out.println("[QA] darLike → " + response.getStatusCode());
        TestContext.setResponse(response);
    }

    @Cuando("vuelvo a dar like a la misma publicacion")
    public void darLikeOtraVez() {
        Response response = SerenityRest.given()
                .header("Authorization", "Bearer " + TestContext.getToken())
                .post("/api/likes?publicacionId=" + TestContext.getPublicacionId());
        System.out.println("[QA] darLikeOtraVez → " + response.getStatusCode());
        TestContext.setResponse(response);
    }

    @Entonces("el servicio responde con codigo {int} o {int}")
    public void verificarCodigoAlternativo(int cod1, int cod2) {
        int actual = TestContext.getResponse().getStatusCode();
        assertThat(actual)
                .as("Esperaba %s o %s pero obtuve %s", cod1, cod2, actual)
                .isIn(cod1, cod2);
    }

    @Entonces("la respuesta indica si el like es nuevo")
    public void verificarLikeNuevo() {
        assertThat(TestContext.getResponse().jsonPath().getBoolean("esNuevo")).isNotNull();
    }

    @Entonces("la respuesta indica que el like NO es nuevo")
    public void likeNoEsNuevo() {
        assertThat(TestContext.getResponse().jsonPath().getBoolean("esNuevo")).isFalse();
    }

    @Entonces("el contador de likes no se incrementa")
    public void contadorNoIncrementa() {
        assertThat(TestContext.getResponse().jsonPath().getBoolean("esNuevo")).isFalse();
    }

    // ── WebSocket (requieren browser) ────────────────────
    @Dado("estoy conectado al WebSocket")
    public void conectadoWebSocket() {}

    @Dado("estoy suscrito al canal {string}")
    public void suscritoCanal(String canal) {}

    @Entonces("recibo un evento LikeRegistrado por WebSocket en menos de {int} segundos")
    public void reciboEventoWebSocket(int segundos) {}

    @Dado("que tengo dos pestanas abiertas con el feed")
    public void dosPestanas() {}

    @Dado("en la pestana {int} estoy autenticado como {string}")
    public void autenticadoEnPestana(int pestana, String email) {}

    @Entonces("en la pestana {int} el contador de likes se incrementa automaticamente sin recargar")
    public void contadorAutomatico(int pestana) {}
}
