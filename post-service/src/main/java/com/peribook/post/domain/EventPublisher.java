package com.peribook.post.domain;

/**
 * Puerto (interfaz de dominio) para la publicacion de eventos de dominio.
 * <p>
 * Esto es un claro ejemplo del patron Hexagonal (Arquitectura de Puertos y Adaptadores).
 * La capa de dominio declara la interfaz {@code EventPublisher} sin saber nada sobre
 * colas, exchanges, routing keys o cualquier tecnologia de mensajeria. La implementacion
 * concreta vive en {@code infrastructure/messaging/RabbitEventPublisher} y es injectada
 * por Spring en tiempo de ejecucion.
 * <p>
 * Decidi que el dominio dependa de esta abstraccion en lugar de acoplarse directamente
 * a RabbitMQ por dos motivos: (1) los tests unitarios del caso de uso pueden mockear
 * esta interfaz sin levantar un broker de mensajeria, y (2) si en el futuro migramos
 * a Kafka, NATS o cualquier otra tecnologia, el dominio no se entera del cambio.
 *
 * @author Alexander Rubio Caceres
 */
public interface EventPublisher {

    /**
     * Publica un evento de dominio para que otros servicios (feed, notificaciones, etc.)
     * lo consuman de forma asincrona.
     *
     * @param evento el evento de dominio a publicar (nunca null)
     */
    void publish(PublicacionCreada evento);
}
