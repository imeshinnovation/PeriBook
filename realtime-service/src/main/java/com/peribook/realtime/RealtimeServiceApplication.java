package com.peribook.realtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del servicio realtime-service.
 * <p>
 * Este servicio es el encargado de manejar conexiones WebSocket con los clientes
 * y de actuar como puente entre los eventos internos del sistema (provenientes de
 * RabbitMQ) y el feed en vivo que ven los usuarios. Decidí separarlo como un
 * microservicio independiente y no acoplarlo a post-service ni a like-service
 * porque las conexiones WebSocket tienen un ciclo de vida distinto al de las
 * APIs REST: mantener conexiones abiertas implica un uso intensivo de memoria
 * y threads, y no quería que eso afectara la disponibilidad de los endpoints
 * sincrónicos.
 * </p>
 * <p>
 * {@code @SpringBootApplication} escanea el paquete {@code com.peribook.realtime}
 * y sus subpaquetes, por lo que los beans de configuración (RabbitMQ, WebSocket,
 * seguridad) y los consumidores de mensajes se registran automáticamente sin
 * necesidad de {@code @ComponentScan} explícito.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@SpringBootApplication
public class RealtimeServiceApplication {

    /**
     * Arranque del contenedor Spring Boot.
     * <p>
     * Prefiero {@code SpringApplication.run()} a la construcción manual del
     * contexto porque ya integra el soporte para perfiles, la gestión de
     * propiedades externalizadas y los hooks de cierre graceful.
     * </p>
     *
     * @param args argumentos de línea de comandos (se pasan a Spring Boot)
     */
    public static void main(String[] args) {
        SpringApplication.run(RealtimeServiceApplication.class, args);
    }
}
