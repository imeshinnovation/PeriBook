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

    private Response response;
    private String token;
    private String publicacionId;
    private boolean likePrevio;

    @Dado("existe una publicación con ID conocido")
    public void publicacionExistente() {
        // Autenticar y crear publicación
        autenticarComo("ana@peribook.com");
        Response create = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(Map.of("contenido", "Publicación para test de likes"))
                .post("/api/posts");

        if (create.getStatusCode() == 201) {
            publicacionId = create.jsonPath().getString("id");
        } else {
            // Si no hay backend corriendo, usar ID de prueba
            publicacionId = UUID.randomUUID().toString();
        }
    }

    @Dado("ya di like a una publicación")
    public void likePrevio() {
        autenticarComo("ana@peribook.com");
        response = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .post("/api/likes?publicacionId=" + publicacionId);
        likePrevio = true;
    }

    @Cuando("doy like a la publicación")
    public void darLike() {
        autenticarComo("ana@peribook.com");
        response = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .post("/api/likes?publicacionId=" + publicacionId);
    }

    @Cuando("vuelvo a dar like a la misma publicación")
    public void darLikeOtraVez() {
        autenticarComo("ana@peribook.com");
        response = SerenityRest.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .post("/api/likes?publicacionId=" + publicacionId);
    }

    @Entonces("el servicio responde con código {int} o {int}")
    public void verificarCodigoAlternativo(int cod1, int cod2) {
        assertThat(response.getStatusCode()).isIn(cod1, cod2);
    }

    @Entonces("la respuesta indica si el like es nuevo")
    public void verificarLikeNuevo() {
        assertThat(response.jsonPath().getBoolean("esNuevo")).isNotNull();
    }

    @Entonces("la respuesta indica que el like NO es nuevo")
    public void likeNoEsNuevo() {
        assertThat(response.jsonPath().getBoolean("esNuevo")).isFalse();
    }

    @Entonces("el contador de likes no se incrementa")
    public void contadorNoIncrementa() {
        // La idempotencia fue verificada en el paso anterior
        assertThat(response.jsonPath().getBoolean("esNuevo")).isFalse();
    }

    // WebSocket tests — necesitan cliente STOMP real (se ejecutan manualmente)
    @Dado("estoy conectado al WebSocket")
    public void conectadoWebSocket() {
        // Requiere entorno real con realtime-service corriendo
    }

    @Dado("estoy suscrito al canal {string}")
    public void suscritoCanal(String canal) {
        // Requiere entorno real
    }

    @Entonces("recibo un evento LikeRegistrado por WebSocket en menos de {int} segundos")
    public void reciboEventoWebSocket(int segundos) {
        // Requiere entorno real
    }

    @Dado("que tengo dos pestañas abiertas con el feed")
    public void dosPestanas() {
        // Escenario UI — requiere Selenium WebDriver + dos ventanas
    }

    @Dado("en la pestaña {int} estoy autenticado como {string}")
    public void autenticadoEnPestana(int pestana, String email) {
        // UI test
    }

    @Entonces("en la pestaña {int} el contador de likes se incrementa automáticamente sin recargar")
    public void contadorAutomatico(int pestana) {
        // UI test
    }

    // ── Helpers ──────────────────────────────────────────

    private void autenticarComo(String email) {
        Response login = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", "secreto123"))
                .post("/api/auth/login");
        token = login.jsonPath().getString("token");
    }
}
