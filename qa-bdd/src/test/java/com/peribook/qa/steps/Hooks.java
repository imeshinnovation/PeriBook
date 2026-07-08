package com.peribook.qa.steps;

import io.cucumber.java.Before;
import net.serenitybdd.rest.SerenityRest;

/**
 * Configuración global para los steps de prueba.
 * REST Assured apunta al API Gateway (único punto de entrada).
 */
public class Hooks {

    @Before
    public void setup() {
        SerenityRest.setDefaultBasePath("");
        // La base URL se configura en serenity.conf:
        //   default → http://localhost:8080 (api-gateway)
        //   docker  → http://api-gateway:8080
    }
}
