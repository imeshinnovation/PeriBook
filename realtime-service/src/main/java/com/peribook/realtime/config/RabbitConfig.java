package com.peribook.realtime.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el servicio de tiempo real.
 * 

 * Definí un exchange de tipo  porque permite enrutar
 * mensajes por patrón de routing key, lo que nos da flexibilidad para que
 * múltiples servicios consuman el mismo tipo de evento con bindings distintos.
 * Un exchange  también habría servido si todas las colas
 * recibieran todos los mensajes, pero con topic podemos granular: por ejemplo,
 * añadir una cola  en el futuro sin tocar las otras
 * colas ni cambiar el exchange.
 * 
 * 

 * Usé  en  porque
 * estos beans son simples llamadas a constructores de Spring AMQP sin
 * dependencias entre ellos que requieran interceptación. Esto evita la
 * creación del proxy CGLIB y reduce el tiempo de arranque del contexto.
 * 
 *
 * @author Alexander Rubio Caceres
 */
@Configuration(proxyBeanMethods = false)
public class RabbitConfig {

    /** Nombre del exchange tópico compartido. Todos los servicios del ecosistema publican aquí. */
    public static final String EXCHANGE = "peribook.events";

    /** Cola para eventos de publicación creada. Alimenta el feed en vivo de los clientes. */
    public static final String QUEUE_FEED = "realtime.feed";

    /** Cola para eventos de like registrado. Se reenvía a los clientes para actualizar el feed. */
    public static final String QUEUE_LIKES = "realtime.likes";

    /**
     * Exchange tópico compartido para todos los eventos de dominio en PeriBook.
     * 

     * Lo configuro como durable () para que sobreviva a reinicios del
     * broker, pero no auto-delete () porque quiero que el exchange
     * persista aunque no haya colas vinculadas temporalmente.
     * 
     *
     * @return el exchange tópico del sistema
     */
    @Bean
    public TopicExchange peribookExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    /**
     * Cola durable para los eventos del feed del usuario.
     * 

     *  asegura que los mensajes no se pierdan si el servicio
     * realtime-service se reinicia brevemente — RabbitMQ los retiene hasta que
     * el consumidor los acknowledge.
     * 
     *
     * @return cola del feed
     */
    @Bean
    public Queue feedQueue() {
        return new Queue(QUEUE_FEED, true);
    }

    /**
     * Cola durable para los eventos de likes.
     * 

     * Separé esta cola de  porque los eventos de like pueden
     * tener un volumen distinto y una prioridad de procesamiento diferente.
     * Si en el futuro queremos escalar el consumo de likes por separado, tener
     * colas independientes nos lo permite sin tocar la configuración del feed.
     * 
     *
     * @return cola de likes
     */
    @Bean
    public Queue likesQueue() {
        return new Queue(QUEUE_LIKES, true);
    }

    /**
     * Vincula la cola del feed al exchange con la routing key .
     * 

     * Solo los mensajes publicados con esa routing key exacta llegarán a
     * , lo que evita que eventos irrelevantes (como un
     * like) consuman recursos en este consumidor.
     * 
     *
     * @param feedQueue la cola del feed
     * @param exchange  el exchange tópico
     * @return binding entre la cola y el exchange
     */
    @Bean
    public Binding feedBinding(Queue feedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(feedQueue).to(exchange).with("publicacion.creada");
    }

    /**
     * Vincula la cola de likes al exchange con la routing key .
     *
     * @param likesQueue la cola de likes
     * @param exchange   el exchange tópico
     * @return binding entre la cola y el exchange
     */
    @Bean
    public Binding likesBinding(Queue likesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(likesQueue).to(exchange).with("like.registrado");
    }
}
