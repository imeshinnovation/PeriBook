package com.peribook.post.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de RabbitMQ para el post-service.
 * 

 * Use  en la anotacion  para que
 * Spring no genere proxies CGLIB sobre esta clase. Esto es una optimizacion: como aqui
 * todos los beans se usan directamente (no hay llamadas internas entre metodos @Bean),
 * no necesito el costo extra de los proxies. Es un detalle que aprendi depurando
 * problemas de rendimiento en arranque.
 * 

 * Defino un exchange de tipo TopicExchange durable (true) y no-auto-delete (false).
 * El patron Topic me permite que los consumidores se suscriban con routing keys
 * parciales (ej: "*.creada" para recibir todo evento de creacion) sin que el publicador
 * necesite conocer a los suscriptores. Esto es fundamental para un sistema basado en
 * eventos como PeriBook.
 * 

 * Las colas  y  se declaran aqui mismo para
 * que existan desde el primer despliegue. El servicio de feed-service las consumira
 * para actualizar el timeline en tiempo real. Esto evita el problema clasico de
 * "publicar antes de que la cola exista".
 *
 * @author Alexander Rubio Caceres
 */
@Configuration(proxyBeanMethods = false)
public class RabbitConfig {

    /** Nombre del exchange topico donde se publican todos los eventos de PeriBook. */
    public static final String EXCHANGE_NAME = "peribook.events";

    /**
     * Exchange topico durable para eventos del dominio.
     * Duradero: sobrevive a reinicios del broker.
     * No auto-delete: persiste aunque no haya colas vinculadas.
     */
    @Bean
    public TopicExchange peribookExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * Cola para eventos del feed en tiempo real.
     * El feed-service consume de esta cola para actualizar el timeline de los usuarios.
     */
    @Bean
    public Queue feedQueue() { return new Queue("realtime.feed", true); }

    /**
     * Cola para eventos de "me gusta" en tiempo real.
     * Separada de la cola de feed para que el procesamiento de likes no compita
     * con el de publicaciones nuevas.
     */
    @Bean
    public Queue likesQueue() { return new Queue("realtime.likes", true); }

    /**
     * Vincula la cola de feed al exchange con la routing key "publicacion.creada".
     * Esto asegura que cada vez que se publique una nueva publicacion, el evento
     * llegue a la cola de feed para su procesamiento.
     */
    @Bean
    public Binding feedBinding(Queue feedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(feedQueue).to(exchange).with("publicacion.creada");
    }

    /**
     * Vincula la cola de likes al exchange con la routing key "like.registrado".
     * Separada de la binding de publicaciones para mantener independencia de
     * procesamiento entre distintos tipos de eventos.
     */
    @Bean
    public Binding likesBinding(Queue likesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(likesQueue).to(exchange).with("like.registrado");
    }
}
