package com.peribook.like.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de RabbitMQ para el microservicio Like.
 * <p>
 * Defino aqui el exchange topic {@code peribook.events} y las colas que este
 * servicio necesita que existan. Use {@code proxyBeanMethods = false} porque
 * este {@code @Configuration} no requiere que Spring CGLIB intercepte las
 * llamadas entre metodos @Bean — cada {@code @Bean} es independiente.
 * </p>
 * <p>
 * Decidi declarar las colas (feed, likes) y sus bindings aqui mismo, en lugar
 * de hacerlo solo desde el consumidor, para evitar la condicion de carrera
 * donde el publisher envia un evento antes de que el consumidor declare la
 * cola. Con RabbitMQ topic exchange, si la cola no existe el mensaje se pierde.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@Configuration(proxyBeanMethods = false)
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "peribook.events";

    /**
     * Exchange topic duradero (durable=true, autoDelete=false).
     * Los exchanges topic me permiten rutear mensajes por patrones de routing key,
     * util cuando el ecosistema de microservicios crezca con mas tipos de eventos.
     */
    @Bean
    public TopicExchange peribookExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    // Declaro las colas explicitamente para que existan antes de publicar.
    // Esto evita perdida de eventos si el publisher arranca antes que
    // el consumidor (realtime-service).
    @Bean
    public Queue feedQueue() { return new Queue("realtime.feed", true); }

    @Bean
    public Queue likesQueue() { return new Queue("realtime.likes", true); }

    @Bean
    public Binding feedBinding(Queue feedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(feedQueue).to(exchange).with("publicacion.creada");
    }

    @Bean
    public Binding likesBinding(Queue likesQueue, TopicExchange exchange) {
        // Routing key "like.registrado" — el mismo que usa RabbitEventPublisher.
        return BindingBuilder.bind(likesQueue).to(exchange).with("like.registrado");
    }
}
