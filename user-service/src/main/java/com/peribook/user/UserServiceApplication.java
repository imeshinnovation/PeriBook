package com.peribook.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio user-service.
 * 

 * No necesita ,  ni nada adicional
 * porque Spring Boot 3 detecta automáticamente las dependencias en el classpath y configura
 * lo que haga falta. Decidí mantenerlo limpio intencionalmente — un solo microservicio no
 * debería arrastrar configuración global que no necesita.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        // Dejo que Spring Boot haga su magia: auto-configuration, component scan, etc.
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
