package com.peribook.qa.steps;

import io.cucumber.java.Before;
import io.restassured.RestAssured;

/**
 * Configuración global para todas las pruebas.
 * REST Assured apunta de caja negra al API Gateway.
 */
public class Hooks {

    @Before(order = 0)
    public void setup() {
        String apiBaseUrl = System.getProperty("api.base.url", "http://localhost:8080");

        RestAssured.baseURI = apiBaseUrl;

        System.out.println("[QA] API base URL: " + apiBaseUrl);
    }
}
<!-- 2026-07-09 -->
