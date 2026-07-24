package com.peribook.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de bff-web, el Backend For Frontend de PeriBook.
 * 

 * Decidi hacer un BFF y no un API Gateway tradicional porque el BFF encapsula
 * logica de orquestacion que es especifica del frontend: el feed enriquecido combina
 * datos de tres servicios (posts, usuarios, likes) en una sola llamada. Un gateway
 * generico delegaria esa responsabilidad al cliente, y no quiero que una SPA tenga
 * que hacer tres round-trips.
 * 
 * 

 * Uso  para que Spring Boot auto-configure el contexto,
 * reactive web (WebFlux) y discovery si hiciera falta. La aplicacion es reactiva de
 * punta a punta para no bloquear hilos mientras esperamos respuestas de los servicios
 * internos.
 * 
 *
 * @author Alexander Rubio Caceres
 */
@SpringBootApplication
public class BffWebApplication {

    public static void main(String[] args) {
        // SpringApplication.run() arranca el contexto embedded de Netty (WebFlux)
        // sin servlet container. Al ser reactivo, el pool de hilos es minimo comparado
        // con un Tomcat tradicional.
        SpringApplication.run(BffWebApplication.class, args);
    }
}
