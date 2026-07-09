package com.peribook.qa.steps;

import io.restassured.response.Response;

/**
 * Estado compartido entre todos los step definitions.
 * Cucumber inyecta una sola instancia via constructor en cada step class.
 */
public class TestContext {
    public Response response;
    public String token;
    public String userId;
    public String publicacionId;
}
