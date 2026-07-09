package com.peribook.realtime.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PublicacionCreadaConsumer {

    private static final Logger log = LoggerFactory.getLogger(PublicacionCreadaConsumer.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public PublicacionCreadaConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.feed}")
    public void onPublicacionCreada(String mensaje) {
        try {
            PublicacionCreadaEvent evento = objectMapper.readValue(mensaje, PublicacionCreadaEvent.class);
            log.info("Recibido PublicacionCreada: {}", evento.publicacionId());
            messagingTemplate.convertAndSend("/topic/feed", evento);
        } catch (Exception e) {
            log.error("Error al procesar PublicacionCreada", e);
        }
    }
}
