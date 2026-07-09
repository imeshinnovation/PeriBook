package com.peribook.post.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "peribook.events";
    public static final String QUEUE_FEED = "realtime.feed";
    public static final String QUEUE_LIKES = "realtime.likes";

    @Bean
    public TopicExchange peribookExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    // Declarar colas y bindings aquí también para que existan antes de publicar
    @Bean
    public Queue feedQueue() { return new Queue(QUEUE_FEED, true); }

    @Bean
    public Queue likesQueue() { return new Queue(QUEUE_LIKES, true); }

    @Bean
    public Binding feedBinding(Queue feedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(feedQueue).to(exchange).with("publicacion.creada");
    }

    @Bean
    public Binding likesBinding(Queue likesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(likesQueue).to(exchange).with("like.registrado");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
