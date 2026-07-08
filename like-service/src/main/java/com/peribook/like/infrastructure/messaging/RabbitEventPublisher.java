package com.peribook.like.infrastructure.messaging;

import com.peribook.like.domain.EventPublisher;
import com.peribook.like.domain.LikeRegistrado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);
    private static final String EXCHANGE = "peribook.events";
    private static final String ROUTING_KEY = "like.registrado";

    private final RabbitTemplate rabbitTemplate;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(LikeRegistrado evento) {
        log.info("Publicando evento LikeRegistrado: {}", evento.likeId());
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, evento);
    }
}
