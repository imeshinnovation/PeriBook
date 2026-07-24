package com.peribook.gateway;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Agregador de documentacion OpenAPI/Swagger para todos los microservicios.
 * <p>
 * En lugar de tener que abrir cinco URLs distintas para ver la documentacion
 * de cada servicio, centralizo aqui todo el catalogo de endpoints. Springdoc
 * hace el trabajo pesado: cada microservicio expone su propio
 * {@code /v3/api-docs}, y el gateway recolecta esos endpoints y los agrupa
 * bajo una sola interfaz de Swagger UI.
 * <p>
 * Use {@code proxyBeanMethods = false} por la misma razon que en
 * GatewayRoutes: no hay dependencias entre estos beans, asi que evito el
 * overhead de los proxies CGLIB.
 * <p>
 * Decidi incluir tambien el BFF como un grupo separado porque, aunque el BFF
 * no es un microservicio de dominio en si mismo, sus endpoints son los que
 * consume el frontend y necesitan estar documentados para los desarrolladores
 * del cliente web.
 *
 * @author Alexander Rubio Caceres
 */
@Configuration(proxyBeanMethods = false)
public class SwaggerAggregator {

    /**
     * Metadatos globales de la API.
     * <p>
     * La informacion que pongo aqui (titulo, descripcion, version) aparece en
     * la cabecera de Swagger UI. Mantengo la version alineada con la del
     * proyecto para que quede claro que documentacion corresponde a que
     * release.
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PeriBook API")
                        .description("Documentacion agregada de todos los microservicios de PeriBook")
                        .version("1.0.0"));
    }

    /**
     * Grupo de endpoints del servicio de autenticacion.
     * <p>
     * Cada grupo se define con un nombre unico y un patron de path. Springdoc
     * se encarga de consultar el {@code /v3/api-docs} del servicio
     * correspondiente (a traves del ruteo del gateway) y mostrar solo los
     * endpoints que coinciden con el patron.
     */
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

    /**
     * Grupo del BFF Web.
     * <p>
     * Inclui el BFF como un grupo aparte porque estos endpoints son los que
     * realmente consume el frontend. Aunque internamente el BFF orquesta
     * llamadas a otros servicios, la interfaz publica que ve el frontend es
     * la del BFF, y por eso merece su propia seccion en la documentacion.
     */
    @Bean
    public GroupedOpenApi bffApi() {
        return GroupedOpenApi.builder()
                .group("bff-web")
                .displayName("BFF Web")
                .pathsToMatch("/bff/**")
                .build();
    }
}
