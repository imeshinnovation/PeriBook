package com.peribook.realtime.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeRegistradoConsumer {

    private static final Logger log = LoggerFactory.getLogger(LikeRegistradoConsumer.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public LikeRegistradoConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.likes}")
    public void onLikeRegistrado(String mensaje) {
        try {
            LikeRegistradoEvent evento = objectMapper.readValue(mensaje, LikeRegistradoEvent.class);
            log.info("Recibido LikeRegistrado: publicacion={}, usuario={}",
                    evento.publicacionId(), evento.usuarioId());
            // Enviar también a /topic/feed para que todos los clientes reciban la actualización
            messagingTemplate.convertAndSend("/topic/feed", evento);
        } catch (Exception e) {
            log.error("Error al procesar LikeRegistrado", e);
        }
    }
}
<!-- 2026-07-09 -->
