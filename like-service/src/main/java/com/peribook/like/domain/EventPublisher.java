package com.peribook.like.domain;

/**
 * Puerto de salida para la publicacion de eventos de dominio.
 * 

 * Al igual que LikeRepository, esta interfaz pertenece al dominio para
 * no acoplar el caso de uso a un broker de mensajeria concreto. La implementacion
 * con RabbitMQ () se inyecta desde infraestructura.
 * Si en el futuro quisiera cambiar a Kafka, Pulsar o incluso una cola en memoria
 * para pruebas, solo necesito escribir un nuevo adaptador que implemente esta interfaz.
 * 
 *
 * @author Alexander Rubio Caceres
 */
public interface EventPublisher {
    /**
     * Publica un evento de dominio para que otros servicios (o el mismo)
     * reaccionen de forma asincrona.
     *
     * @param evento el evento a publicar
     */
    void publish(LikeRegistrado evento);
}
