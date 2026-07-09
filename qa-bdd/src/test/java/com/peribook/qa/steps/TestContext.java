package com.peribook.qa.steps;

import io.restassured.response.Response;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.environment.SystemEnvironmentVariables;

/**
 * Estado compartido entre step definitions usando Serenity session.
 * Alternativa a PicoContainer que no requiere dependencia extra.
 */
public class TestContext {

    private static final EnvironmentVariables ENV =
            SystemEnvironmentVariables.createEnvironmentVariables();

    public static String getApiBaseUrl() {
        return ENV.getProperty("api.base.url", "http://localhost:8080");
    }

    // ── Session keys ──────────────────────────────────
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PUBLICACION_ID = "publicacionId";

    public static Response getResponse() {
        return (Response) net.serenitybdd.core.Serenity.getCurrentSession().get(KEY_RESPONSE);
    }

    public static void setResponse(Response response) {
        net.serenitybdd.core.Serenity.getCurrentSession().put(KEY_RESPONSE, response);
    }

    public static String getToken() {
        return (String) net.serenitybdd.core.Serenity.getCurrentSession().get(KEY_TOKEN);
    }

    public static void setToken(String token) {
        net.serenitybdd.core.Serenity.getCurrentSession().put(KEY_TOKEN, token);
    }

    public static String getUserId() {
        return (String) net.serenitybdd.core.Serenity.getCurrentSession().get(KEY_USER_ID);
    }

    public static void setUserId(String userId) {
        net.serenitybdd.core.Serenity.getCurrentSession().put(KEY_USER_ID, userId);
    }

    public static String getPublicacionId() {
        return (String) net.serenitybdd.core.Serenity.getCurrentSession().get(KEY_PUBLICACION_ID);
    }

    public static void setPublicacionId(String id) {
        net.serenitybdd.core.Serenity.getCurrentSession().put(KEY_PUBLICACION_ID, id);
    }
}
<!-- 2026-07-09 -->
