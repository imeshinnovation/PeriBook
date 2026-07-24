package com.peribook.realtime.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos {@code LikeRegistrado} desde RabbitMQ.
 * <p>
 * Este componente es el puente entre el bus de eventos interno y los clientes
 * conectados por WebSocket. Cuando un usuario da "like" a una publicación en
 * like-service, ese servicio publica un evento en RabbitMQ. Este consumidor lo
 * recibe, lo deserializa y lo reenvía a todos los clientes suscritos al topic
 * {@code /topic/feed} para que el feed se actualice en tiempo real.
 * </p>
 * <p>
 * Decidí hacer la deserialización manual con {@code ObjectMapper} en lugar de
 * usar el convertidor automático de Spring AMQP ({@code Jackson2JsonMessageConverter})
 * para tener control explícito sobre la configuración del mapper (por ejemplo,
 * el registro del {@code JavaTimeModule}) y evitar sorpresas con la
 * configuración global del contexto de Spring.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@Component
public class LikeRegistradoConsumer {

    private static final Logger log = LoggerFactory.getLogger(LikeRegistradoConsumer.class);

    /** Plantilla de mensajería STOMP para enviar mensajes a los clientes WebSocket. */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Mapper JSON configurado manualmente con soporte para Java 8+ Time API.
     * <p>
     * Creo una instancia propia en lugar de inyectar el {@code ObjectMapper}
     * de Spring porque solo necesito una configuración minimalista sin los
     * módulos adicionales que Spring Boot registra por defecto (como
     * Jackson Annotation Introspector). Esto también hace el componente
     * más predecible y fácil de testear.
     * </p>
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructor que recibe la plantilla STOMP inyectada por Spring.
     * <p>
     * Inicializo el {@code ObjectMapper} interno con el módulo
     * {@code JavaTimeModule} para que serialize/deserialize correctamente
     * los campos {@code Instant} del evento, que es el tipo que uso para
     * representar momentos en el tiempo por ser timezone-agnostic.
     * </p>
     *
     * @param messagingTemplate plantilla STOMP para enviar mensajes a los clientes WebSocket
     */
    public LikeRegistradoConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Procesa un evento {@code LikeRegistrado} recibido de la cola de RabbitMQ.
     * <p>
     * El nombre de la cola se toma de la propiedad {@code app.rabbitmq.queue.likes}
     * para mantener la configuración externalizada y permitir cambiar la cola
     * sin recompilar.
     * </p>
     * <p>
     * El flujo es:
     * <ol>
     *   <li>Deserializar el JSON del mensaje a un {@code LikeRegistradoEvent}</li>
     *   <li>Registrar en log la recepción del evento (auditoría básica)</li>
     *   <li>Reenviar el evento a {@code /topic/feed} para que todos los clientes
     *       conectados reciban la actualización inmediatamente</li>
     * </ol>
     * Envío los likes al mismo topic {@code /topic/feed} que las publicaciones
     * nuevas para que el frontend tenga un único canal de suscripción y pueda
     * diferenciar los tipos de evento por la estructura del JSON recibido.
     * </p>
     *
     * @param mensaje el mensaje JSON recibido de la cola de RabbitMQ
     */
    @RabbitListener(queues = "${app.rabbitmq.queue.likes}")
    public void onLikeRegistrado(String mensaje) {
        try {
            LikeRegistradoEvent evento = objectMapper.readValue(mensaje, LikeRegistradoEvent.class);
            log.info("Recibido LikeRegistrado: publicacion={}, usuario={}",
                    evento.publicacionId(), evento.usuarioId());
            // Reenviar a /topic/feed para que todos los clientes reciban la actualización en vivo
            messagingTemplate.convertAndSend("/topic/feed", evento);
        } catch (Exception e) {
            log.error("Error al procesar LikeRegistrado", e);
        }
    }
}
