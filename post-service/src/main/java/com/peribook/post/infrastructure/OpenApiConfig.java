package com.peribook.post.infrastructure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI postOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("post-service")
                        .description("Publicaciones de usuarios")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://192.168.50.31:8080").description("Gateway - produccion")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido en auth-service")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
