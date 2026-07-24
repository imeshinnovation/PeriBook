package com.peribook.bff.infrastructure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bffOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("bff-web")
                        .description("Backend For Frontend — feed enriquecido")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://192.168.50.31:8080").description("Gateway - produccion")));
    }
}
