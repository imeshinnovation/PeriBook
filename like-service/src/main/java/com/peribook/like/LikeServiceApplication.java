package com.peribook.like;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio Like dentro de la red social PeriBook.
 * 

 * Es el microservicio encargado de gestionar los "me gusta" de las
 * publicaciones. La anotacion  activa la
 * configuracion automatica, el escaneo de componentes y la habilitacion
 * de las capacidades de Spring Boot.
 * 
 *
 * @author Alexander Rubio Caceres
 */
@SpringBootApplication
public class LikeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LikeServiceApplication.class, args);
    }
}
