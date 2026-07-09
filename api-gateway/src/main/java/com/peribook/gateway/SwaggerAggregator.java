package com.peribook.gateway;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SwaggerAggregator {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PeriBook API")
                        .description("Documentación agregada de todos los microservicios de PeriBook")
                        .version("1.0.0"));
    }

    @Bean
    public GroupedOpenApi authServiceApi() {
        return GroupedOpenApi.builder()
                .group("auth-service")
                .displayName("Auth Service")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userServiceApi() {
        return GroupedOpenApi.builder()
                .group("user-service")
                .displayName("User Service")
                .pathsToMatch("/api/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi postServiceApi() {
        return GroupedOpenApi.builder()
                .group("post-service")
                .displayName("Post Service")
                .pathsToMatch("/api/posts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi likeServiceApi() {
        return GroupedOpenApi.builder()
                .group("like-service")
                .displayName("Like Service")
                .pathsToMatch("/api/likes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi bffApi() {
        return GroupedOpenApi.builder()
                .group("bff-web")
                .displayName("BFF Web")
                .pathsToMatch("/bff/**")
                .build();
    }
}
<!-- 2026-07-09 -->
