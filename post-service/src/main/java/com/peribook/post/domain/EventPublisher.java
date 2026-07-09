package com.peribook.post.domain;

/**
 * Puerto para publicación de eventos de dominio.
 * La implementación concreta usa RabbitMQ (infrastructure/messaging).
 */
public interface EventPublisher {
    void publish(PublicacionCreada evento);
}
<!-- 2026-07-09 -->
