package com.peribook.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Punto de entrada del API Gateway de PeriBook.
 * <p>
 * Este microservicio es la puerta de entrada única a todo el sistema. Decidí
 * centralizar aquí el ruteo, la autenticación y la agregación de documentación
 * por una razón muy concreta: evitar que cada microservicio tenga que
 * implementar su propia seguridad y CORS. Con un solo gateway, cualquier cambio
 * en la política de acceso se hace en un solo lugar y se replica a todos los
 * servicios de inmediato.
 * <p>
 * Uso {@code @EnableConfigurationProperties(JwtConfig.class)} porque las
 * propiedades JWT (issuer, expiración, rutas de las llaves RSA) las mantengo
 * externalizadas en application.yml. No me gusta tener secretos quemados en el
 * código — si mañana cambia la rotación de llaves, solo toca el YAML.
 *
 * @author Alexander Rubio Cáceres
 */
@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class ApiGatewayApplication {

    /**
     * Arranque estándar de Spring Boot. Nada fuera de lo común aquí — levanto el
     * contexto de la aplicación, se disparan los beans de rutas, seguridad y
     * Swagger que definí en las clases companion, y el gateway queda escuchando
     * en el puerto configurado.
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
