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

 * Este componente es el gemelo de LikeRegistradoConsumer pero para el
 * evento de creación de publicaciones. Cuando post-service publica una nueva
 * publicación, este consumidor la recibe de la cola  y la
 * reenvía a todos los clientes WebSocket conectados al topic .
 * 
 * 

 * Separé los consumidores por tipo de evento (uno para publicaciones, otro para
 * likes) en lugar de tener un consumidor genérico que discrimine por routing
 * key porque esto facilita el mantenimiento: cada clase tiene una única
 * responsabilidad y se puede modificar, testear o escalar de forma independiente.
 * Además, si un tipo de evento falla (por ejemplo, un error de deserialización),
 * el otro sigue funcionando sin verse afectado.
 * 
 *
 * @author Alexander Rubio Caceres
 */
@Component
public class PublicacionCreadaConsumer {

    private static final Logger log = LoggerFactory.getLogger(PublicacionCreadaConsumer.class);

    /** Plantilla de mensajería STOMP para enviar mensajes a los clientes WebSocket. */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Mapper JSON configurado manualmente con soporte para Java 8+ Time API.
     * 

     * Igual que en LikeRegistradoConsumer, uso una instancia propia
     * de  para evitar depender de la configuración global
     * de Spring y tener un control preciso sobre la serialización.
     * 
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructor que recibe la plantilla STOMP inyectada por Spring.
     * 

     * Configuro el  con el módulo 
     * necesario para deserializar los campos  que vienen en el
     * JSON del evento. Sin este módulo, Jackson lanza una excepción al
     * encontrar tipos de .
     * 
     *
     * @param messagingTemplate plantilla STOMP para enviar mensajes a los clientes WebSocket
     */
    public PublicacionCreadaConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Procesa un evento  recibido de la cola de RabbitMQ.
     * 

     * La cola se configura externamente mediante la propiedad
     * , lo que permite cambiar el nombre de la
     * cola por entorno (desarrollo, staging, producción) sin modificar el código.
     * 
     * 

     * El flujo de procesamiento:
     * 
     *    * - Deserializar el mensaje JSON a un 
     *    * - Registrar en log el ID de la publicación recibida (traza de auditoría)
     *    * - Reenviar el evento completo a  para los clientes
     *       conectados
     * 
     * Envío el evento al mismo topic que los likes para simplificar el frontend:
     * el cliente solo necesita suscribirse a un destino STOMP y recibe todos
     * los eventos del feed en vivo.
     * 
     *
     * @param mensaje el mensaje JSON recibido de la cola de RabbitMQ
     */
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
