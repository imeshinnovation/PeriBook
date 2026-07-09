package com.peribook.qa.steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    private final TestContext ctx;

    public LoginSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @Dado("que el usuario {string} existe en el sistema")
    public void usuarioExiste(String email) {
        // Asume que el seeder ya insertó los usuarios de prueba
    }

    @Cuando("intento iniciar sesión con email {string} y contraseña {string}")
    public void login(String email, String password) {
        ctx.response = SerenityRest.given()
                .contentType("application/json")
                .body(Map.of("email", email, "password", password))
                .post("/api/auth/login");
        if (ctx.response.getStatusCode() == 200) {
            ctx.token = ctx.response.jsonPath().getString("token");
            ctx.userId = ctx.response.jsonPath().getString("userId");
        }
    }

    @Entonces("el servicio responde con código {int}")
    public void verificarCodigo(int codigo) {
        assertThat(ctx.response).isNotNull();
        assertThat(ctx.response.getStatusCode()).isEqualTo(codigo);
    }

    @Entonces("la respuesta contiene un token JWT válido")
    public void tokenJwtValido() {
        String token = ctx.response.jsonPath().getString("token");
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Entonces("la respuesta contiene el alias {string}")
    public void verificarAlias(String alias) {
        assertThat(ctx.response.jsonPath().getString("alias")).isEqualTo(alias);
    }

    @Entonces("la respuesta contiene el userId del usuario")
    public void verificarUserId() {
        assertThat(ctx.response.jsonPath().getString("userId")).isNotEmpty();
    }

    @Entonces("el mensaje de error es {string}")
    public void mensajeError(String mensaje) {
        String detail = ctx.response.jsonPath().getString("detail");
        assertThat(detail).contains(mensaje);
    }
}
