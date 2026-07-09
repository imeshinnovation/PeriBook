package com.peribook.realtime.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeRegistradoConsumer {

    private static final Logger log = LoggerFactory.getLogger(LikeRegistradoConsumer.class);
    private final SimpMessagingTemplate messagingTemplate;

    public LikeRegistradoConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.likes}")
    public void onLikeRegistrado(LikeRegistradoEvent evento) {
        log.info("Recibido LikeRegistrado: publicacion={}, usuario={}",
                evento.publicacionId(), evento.usuarioId());
        // Enviar al canal específico de la publicación
        messagingTemplate.convertAndSend(
                "/topic/publicacion/" + evento.publicacionId() + "/likes", evento);
    }
}
