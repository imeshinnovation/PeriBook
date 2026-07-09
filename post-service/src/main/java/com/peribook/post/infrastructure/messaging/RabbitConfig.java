package com.peribook.post.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "peribook.events";

    @Bean
    public TopicExchange peribookExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    // Declarar colas para que existan antes de publicar
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
        return BindingBuilder.bind(likesQueue).to(exchange).with("like.registrado");
    }
}
<!-- 2026-07-09 -->
