package com.peribook.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Definición declarativa de todas las rutas del gateway hacia los
 * microservicios internos.
 * 

 * Usé  porque este  no
 * necesita que Spring intercepte las llamadas entre sus beans — cada
 *  es independiente y no hay dependencias circulares. Esto
 * reduce el overhead de startup y le dice a Spring que no genere proxies CGLIB
 * innecesarios.
 * 

 * Todas las rutas usan el nombre del servicio Docker Swarm como hostname
 * (ej: ). Esto funciona porque Swarm tiene su
 * propio DNS interno que resuelve los nombres de servicio a los contenedores
 * correspondientes. No necesito un service discovery externo como Eureka
 * mientras el despliegue sea en Swarm.
 * 

 * El orden de las rutas importa: Spring Cloud Gateway evalúa las rutas en el
 * orden en que se registran. Puse primero las rutas de Swagger porque son las
 * más específicas (tienen filtros) y luego los microservicios en orden
 * alfabético para facilitar la legibilidad.
 *
 * @author Alexander Rubio Cáceres
 */
@Configuration(proxyBeanMethods = false)
public class GatewayRoutes {

    /**
     * Registro central de rutas.
     * 

     * Decidí agrupar todas las rutas en un solo método en lugar de dividirlas
     * en varios beans porque así el mapa completo de enrutamiento se ve de un
     * solo vistazo. Cuando un nuevo desarrollador llega al proyecto, abre este
     * archivo y sabe al instante "ah, estos son los servicios que existen".
     * 

     * Uso RouteLocatorBuilder en lugar de anotaciones controller-style
     * porque la sintaxis fluida de Gateway me permite expresar rutas, filtros y
     * reescrituras de path en una sola línea, sin dispersar la configuracion
     * entre varias clases.
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ── Documentacion Swagger unificada ───────────────
                // Swagger UI (HTML, CSS, JS) se sirve desde auth-service
                // porque usa Spring MVC donde springdoc funciona sin problemas.
                // La configuracion de agregacion (urls[]) esta en auth-service.
                .route("swagger-docs", r -> r
                        .path("/docs")
                        .filters(f -> f.rewritePath("/docs", "/swagger-ui/index.html"))
                        .uri("http://auth-service:8081"))

                .route("swagger-ui", r -> r
                        .path("/swagger-ui/**", "/webjars/**")
                        .uri("http://auth-service:8081"))

                // Cada /v3/api-docs/{servicio} se reescribe al /v3/api-docs
                // del microservicio correspondiente.
                .route("auth-service-docs", r -> r
                        .path("/v3/api-docs/auth-service")
                        .filters(f -> f.rewritePath("/v3/api-docs/auth-service", "/v3/api-docs"))
                        .uri("http://auth-service:8081"))

                .route("user-service-docs", r -> r
                        .path("/v3/api-docs/user-service")
                        .filters(f -> f.rewritePath("/v3/api-docs/user-service", "/v3/api-docs"))
                        .uri("http://user-service:8082"))

                .route("post-service-docs", r -> r
                        .path("/v3/api-docs/post-service")
                        .filters(f -> f.rewritePath("/v3/api-docs/post-service", "/v3/api-docs"))
                        .uri("http://post-service:8083"))

                .route("like-service-docs", r -> r
                        .path("/v3/api-docs/like-service")
                        .filters(f -> f.rewritePath("/v3/api-docs/like-service", "/v3/api-docs"))
                        .uri("http://like-service:8084"))

                .route("bff-web-docs", r -> r
                        .path("/v3/api-docs/bff-web")
                        .filters(f -> f.rewritePath("/v3/api-docs/bff-web", "/v3/api-docs"))
                        .uri("http://bff-web:8086"))

                // Catch-all para /v3/api-docs/swagger-config y cualquier
                // otra ruta de springdoc que no sea de un servicio especifico.
                // Va al final para que las rutas por servicio tengan prioridad.
                .route("swagger-config", r -> r
                        .path("/v3/api-docs/**")
                        .uri("http://auth-service:8081"))

                // ── Microservicios de negocio ──────────────────────
                // Cada ruta sigue el mismo patron: un path prefix que
                // identifica el servicio y un forward al contenedor
                // correspondiente en la overlay network de Swarm.
                // Decidi mantener los nombres de ruta consistentes con
                // los nombres de los servicios de Docker para que la
                // correlacion sea obvia en los logs.

                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("http://auth-service:8081"))

                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("http://user-service:8082"))

                .route("post-service", r -> r
                        .path("/api/posts/**")
                        .uri("http://post-service:8083"))

                .route("like-service", r -> r
                        .path("/api/likes/**")
                        .uri("http://like-service:8084"))

                // Las rutas de WebSocket (/ws/**) van al realtime-service.
                // No aplique filtros de reescritura ni autenticacion a nivel
                // de ruta porque el handshake de WebSocket se maneja aparte
                // en SecurityConfig (se deja pasar sin JWT y la autenticacion
                // se hace internamente en el servicio de realtime).
                .route("realtime-service", r -> r
                        .path("/ws/**")
                        .uri("http://realtime-service:8085"))

                // El BFF (Backend For Frontend) es un caso especial: todas
                // las peticiones /bff/** van a el, y es el BFF quien orquesta
                // las llamadas a los microservicios internos. Decidi separar
                // el BFF como un servicio independiente (no como parte del
                // gateway) para que su logica de orquestacion no contamine
                // las responsabilidades de enrutamiento del gateway.
                .route("bff-web", r -> r
                        .path("/bff/**")
                        .uri("http://bff-web:8086"))

                .build();
    }
}
