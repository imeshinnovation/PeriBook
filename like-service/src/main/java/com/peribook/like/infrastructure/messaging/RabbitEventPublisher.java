package com.peribook.like.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.peribook.like.domain.EventPublisher;
import com.peribook.like.domain.LikeRegistrado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de infraestructura que implementa EventPublisher usando
 * RabbitMQ como broker de mensajeria.
 * 

 * Este es el "adapter" en terminos de Hexagonal Architecture: implementa el
 * puerto de salida definido en el dominio. Decidi usar RabbitMQ porque es
 * ligero, maduro y encaja bien con el modelo de topic exchange para eventos
 * de dominio. Si en el futuro el volumen escala, podria reemplazar este
 * adaptador por uno de Kafka sin tocar el caso de uso.
 * 
 *
 * @author Alexander Rubio Caceres
 */
@Component
public class RabbitEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);
    private static final String EXCHANGE = "peribook.events";
    private static final String ROUTING_KEY = "like.registrado";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        // Creo un ObjectMapper propio en lugar de inyectar el de Spring para
        // no depender de la configuracion global de serializacion. Registro
        // JavaTimeModule para que Instant se serialice como ISO-8601 y no como
        // timestamp numerico.
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Serializa el evento a JSON y lo publica en el exchange configurado.
     * Capture la excepcion con un try-catch para que un error de publicacion
     * no propague y tumbe el hilo del caso de uso. En un entorno productivo
     * esto se complementaria con un dead-letter queue y retry.
     */
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
