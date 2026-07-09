package com.peribook.like.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
    private final ObjectMapper objectMapper;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void publish(LikeRegistrado evento) {
        try {
            String json = objectMapper.writeValueAsString(evento);
            log.info("Publicando evento LikeRegistrado: {}", evento.likeId());
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, json);
        } catch (Exception e) {
            log.error("Error al publicar LikeRegistrado", e);
        }
    }
}
<!-- 2026-07-09 -->
