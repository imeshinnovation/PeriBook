package com.peribook.realtime.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RabbitConfig {

    public static final String EXCHANGE = "peribook.events";
    public static final String QUEUE_FEED = "realtime.feed";
    public static final String QUEUE_LIKES = "realtime.likes";

    @Bean
    public TopicExchange peribookExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue feedQueue() {
        return new Queue(QUEUE_FEED, true);
    }

    @Bean
    public Queue likesQueue() {
        return new Queue(QUEUE_LIKES, true);
    }

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
