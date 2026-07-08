package com.peribook.post.infrastructure.messaging;

import com.peribook.post.domain.EventPublisher;
import com.peribook.post.domain.PublicacionCreada;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);
    private static final String EXCHANGE = "peribook.events";
    private static final String ROUTING_KEY = "publicacion.creada";

    private final RabbitTemplate rabbitTemplate;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(PublicacionCreada evento) {
        log.info("Publicando evento PublicacionCreada: {}", evento.publicacionId());
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, evento);
    }
}
