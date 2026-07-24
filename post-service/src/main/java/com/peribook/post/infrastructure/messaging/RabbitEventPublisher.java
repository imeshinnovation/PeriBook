package com.peribook.post.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.peribook.post.domain.EventPublisher;
import com.peribook.post.domain.PublicacionCreada;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador concreto del puerto {@link EventPublisher} usando RabbitMQ.
 * <p>
 * Esta clase es la implementacion real del contrato definido en la capa de dominio.
 * Toma un evento de dominio {@link PublicacionCreada}, lo serializa a JSON y lo
 * publica en el exchange {@code peribook.events} con la routing key
 * {@code publicacion.creada}.
 * <p>
 * Decidi usar {@link ObjectMapper} de Jackson con el modulo {@link JavaTimeModule}
 * para manejar correctamente los tipos {@code java.time.Instant} que usa el record
 * del evento. Sin este modulo, Jackson serializa Instant como un array numerico en
 * lugar de un string ISO-8601, lo cual romperia la compatibilidad con los consumidores.
 * <p>
 * El {@link RabbitTemplate} lo inyecta Spring Boot autoconfigurado, usando las propiedades
 * definidas en application.yml (host, puerto, credenciales, etc.).
 *
 * @author Alexander Rubio Caceres
 */
@Component
public class RabbitEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);
    private static final String EXCHANGE = "peribook.events";
    private static final String ROUTING_KEY = "publicacion.creada";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Constructor que recibe el RabbitTemplate configurado por Spring Boot.
     * <p>
     * Creo una instancia propia de ObjectMapper en lugar de inyectar la de Spring
     * porque necesito el JavaTimeModule y no quiero arriesgarme a que la configuracion
     * global de Jackson de la aplicacion entre en conflicto con la serializacion
     * de eventos.
     */
    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Publica un evento PublicacionCreada en RabbitMQ.
     * <p>
     * Serializa el evento a JSON y lo envia al exchange configurado. Si ocurre
     * cualquier error durante la serializacion o la publicacion, se registra en el
     * log pero no se relanza la excepcion. Esto es intencional: no quiero que un
     * fallo en la mensajeria haga fallar la operacion de creacion de la publicacion
     * principal. El evento podria reintentarse mas tarde con un mecanismo de
     * dead-letter queue.
     *
     * @param evento el evento de dominio a publicar
     */
    @Override
    public void publish(PublicacionCreada evento) {
        try {
            String json = objectMapper.writeValueAsString(evento);
            log.info("Publicando evento PublicacionCreada: {}", evento.publicacionId());
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, json);
        } catch (Exception e) {
            log.error("Error al publicar PublicacionCreada", e);
        }
    }
}
