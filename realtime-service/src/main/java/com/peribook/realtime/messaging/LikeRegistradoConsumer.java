package com.peribook.realtime.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos  desde RabbitMQ.
 * 

 * Este componente es el puente entre el bus de eventos interno y los clientes
 * conectados por WebSocket. Cuando un usuario da "like" a una publicación en
 * like-service, ese servicio publica un evento en RabbitMQ. Este consumidor lo
 * recibe, lo deserializa y lo reenvía a todos los clientes suscritos al topic
 *  para que el feed se actualice en tiempo real.
 * 
 * 

 * Decidí hacer la deserialización manual con  en lugar de
 * usar el convertidor automático de Spring AMQP ()
 * para tener control explícito sobre la configuración del mapper (por ejemplo,
 * el registro del ) y evitar sorpresas con la
 * configuración global del contexto de Spring.
 * 
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
     * 

     * Creo una instancia propia en lugar de inyectar el 
     * de Spring porque solo necesito una configuración minimalista sin los
     * módulos adicionales que Spring Boot registra por defecto (como
     * Jackson Annotation Introspector). Esto también hace el componente
     * más predecible y fácil de testear.
     * 
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructor que recibe la plantilla STOMP inyectada por Spring.
     * 

     * Inicializo el  interno con el módulo
     *  para que serialize/deserialize correctamente
     * los campos  del evento, que es el tipo que uso para
     * representar momentos en el tiempo por ser timezone-agnostic.
     * 
     *
     * @param messagingTemplate plantilla STOMP para enviar mensajes a los clientes WebSocket
     */
    public LikeRegistradoConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Procesa un evento  recibido de la cola de RabbitMQ.
     * 

     * El nombre de la cola se toma de la propiedad 
     * para mantener la configuración externalizada y permitir cambiar la cola
     * sin recompilar.
     * 
     * 

     * El flujo es:
     * 
     *    * - Deserializar el JSON del mensaje a un 
     *    * - Registrar en log la recepción del evento (auditoría básica)
     *    * - Reenviar el evento a  para que todos los clientes
     *       conectados reciban la actualización inmediatamente
     * 
     * Envío los likes al mismo topic  que las publicaciones
     * nuevas para que el frontend tenga un único canal de suscripción y pueda
     * diferenciar los tipos de evento por la estructura del JSON recibido.
     * 
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
