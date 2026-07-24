package com.peribook.post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio post-service dentro de PeriBook.
 * <p>
 * Al usar {@code @SpringBootApplication}, Spring Boot escanea automáticamente los paquetes
 * {@code com.peribook.post.*} en busca de beans, controladores REST, configuraciones y
 * componentes. Decidí no extender esta clase con nada adicional — prefiero mantenerla
 * minimalista y delegar toda la configuración explícita a clases especializadas
 * (SecurityConfig, RabbitConfig, etc.). Esto facilita las pruebas de integración porque
 * se puede arrancar el contexto con slices específicos sin cargar configuraciones
 * innecesarias.
 *
 * @author Alexander Rubio Caceres
 */
@SpringBootApplication
public class PostServiceApplication {

    /**
     * Arranque de la aplicacion. SpringApplication.run() levanta el contexto de Spring,
     * la configuracion embebida de Tomcat y todas las dependencias declaradas.
     *
     * @param args argumentos de linea de comandos (se pasan directamente a Spring Boot)
     */
    public static void main(String[] args) {
        SpringApplication.run(PostServiceApplication.class, args);
    }
}
