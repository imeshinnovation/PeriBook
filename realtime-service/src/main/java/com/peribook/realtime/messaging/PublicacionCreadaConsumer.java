package com.peribook.realtime.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PublicacionCreadaConsumer {

    private static final Logger log = LoggerFactory.getLogger(PublicacionCreadaConsumer.class);
    private final SimpMessagingTemplate messagingTemplate;

    public PublicacionCreadaConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.feed}")
    public void onPublicacionCreada(PublicacionCreadaEvent evento) {
        log.info("Recibido PublicacionCreada: {}", evento.publicacionId());
        messagingTemplate.convertAndSend("/topic/feed", evento);
    }
}
