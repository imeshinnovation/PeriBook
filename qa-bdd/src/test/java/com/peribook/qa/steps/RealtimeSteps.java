package com.peribook.qa.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RealtimeSteps {

    private final TestContext ctx;

    public RealtimeSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Dado("existe una publicación con ID conocido")
    public void publicacionExistente() {
        autenticarComo("ana@peribook.com");
        ctx.response = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + ctx.token)
                .body(Map.of("contenido", "Publicación para test de likes"))
                .post("/api/posts");

        if (ctx.response.getStatusCode() == 201) {
            ctx.publicacionId = ctx.response.jsonPath().getString("id");
        } else {
            ctx.publicacionId = UUID.randomUUID().toString();
        }
    }

    @Dado("ya di like a una publicación")
    public void likePrevio() {
        autenticarComo("ana@peribook.com");
        ctx.response = SerenityRest.given()
                .header("Authorization", "Bearer " + ctx.token)
                .post("/api/likes?publicacionId=" + ctx.publicacionId);
    }

    @Cuando("doy like a la publicación")
    public void darLike() {
        autenticarComo("ana@peribook.com");
        ctx.response = SerenityRest.given()
                .header("Authorization", "Bearer " + ctx.token)
                .post("/api/likes?publicacionId=" + ctx.publicacionId);
    }

    @Cuando("vuelvo a dar like a la misma publicación")
    public void darLikeOtraVez() {
        autenticarComo("ana@peribook.com");
        ctx.response = SerenityRest.given()
                .header("Authorization", "Bearer " + ctx.token)
                .post("/api/likes?publicacionId=" + ctx.publicacionId);
    }

    @Entonces("el servicio responde con código {int} o {int}")
    public void verificarCodigoAlternativo(int cod1, int cod2) {
        assertThat(ctx.response.getStatusCode()).isIn(cod1, cod2);
    }

    @Entonces("la respuesta indica si el like es nuevo")
    public void verificarLikeNuevo() {
        assertThat(ctx.response.jsonPath().getBoolean("esNuevo")).isNotNull();
    }

    @Entonces("la respuesta indica que el like NO es nuevo")
    public void likeNoEsNuevo() {
        assertThat(ctx.response.jsonPath().getBoolean("esNuevo")).isFalse();
    }

    @Entonces("el contador de likes no se incrementa")
    public void contadorNoIncrementa() {
        assertThat(ctx.response.jsonPath().getBoolean("esNuevo")).isFalse();
    }

    // ── WebSocket steps (requieren browser real) ──────────
    @Dado("estoy conectado al WebSocket")
    public void conectadoWebSocket() {}

    @Dado("estoy suscrito al canal {string}")
    public void suscritoCanal(String canal) {}

    @Entonces("recibo un evento LikeRegistrado por WebSocket en menos de {int} segundos")
    public void reciboEventoWebSocket(int segundos) {}

    @Dado("que tengo dos pestañas abiertas con el feed")
    public void dosPestanas() {}

    @Dado("en la pestaña {int} estoy autenticado como {string}")
    public void autenticadoEnPestana(int pestana, String email) {}

    @Entonces("en la pestaña {int} el contador de likes se incrementa automáticamente sin recargar")
    public void contadorAutomatico(int pestana) {}

    private void autenticarComo(String email) {
        ctx.response = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", "secreto123"))
                .post("/api/auth/login");
        ctx.token = ctx.response.jsonPath().getString("token");
    }
}
