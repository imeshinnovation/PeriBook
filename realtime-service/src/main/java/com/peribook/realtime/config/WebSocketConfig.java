package com.peribook.realtime.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración del subsistema WebSocket con STOMP sobre SockJS.
 * 

 * Decidí utilizar STOMP en lugar de WebSocket plano porque STOMP ofrece un
 * modelo de destinos (topics/queues) sobre el protocolo de mensajería, lo que
 * encaja perfectamente con el patrón pub-sub que necesitamos para el feed en
 * vivo. Además, Spring tiene soporte de primera clase con
 * , que me permite enviar mensajes a los clientes
 * desde cualquier bean (como los consumidores de RabbitMQ).
 * 
 * 

 * Uso  en lugar de la configuración de bajo
 * nivel de  porque el message broker maneja
 * automáticamente la suscripción a topics, la entrega y el routing, y es
 * extensible si más adelante necesitamos un broker externo como RabbitMQ STOMP.
 * 
 *
 * @author Alexander Rubio Caceres
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el broker de mensajes en memoria y los prefijos de destino.
     * 

     * Uso un  en memoria en lugar de un broker externo
     * porque para la versión inicial de PeriBook el volumen de mensajes en
     * tiempo real no justifica la complejidad operativa de RabbitMQ STOMP ni
     * de un servicio como Redis Pub/Sub. El broker en memoria es liviano y
     * funciona dentro del mismo proceso. Si el día de mañana escalamos,
     * migrar a RabbitMQ STOMP solo requiere cambiar esta línea.
     * 
     * 

     * Prefljo : todos los mensajes del feed en
     * vivo se publican aquí. Los clientes se suscriben a 
     * para recibir actualizaciones.
     * 
     * 

     * Prefijo : los mensajes que los clientes
     * envían al servidor deben usar este prefijo. Por ahora este servicio es
     * puramente push (del servidor al cliente), pero dejo el prefijo definido
     * por si en el futuro necesitamos comandos del cliente (por ejemplo, un
     * "heartbeat" o una confirmación de lectura).
     * 
     *
     * @param registry el registro del broker de mensajes de Spring
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Broker en memoria para los topics — suficiente para el volumen actual
        registry.enableSimpleBroker("/topic");
        // Prefijo para mensajes entrantes enviados por los clientes vía STOMP
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra el endpoint STOMP sobre SockJS para la conexión WebSocket.
     * 

     * El endpoint  es el punto de entrada del handshake. Uso SockJS
     * porque no todos los navegadores o entornos de red soportan WebSocket
     * nativo (por ejemplo, proxies corporativos que bloquean el upgrade de
     * HTTP). SockJS proporciona fallback a transporte XHR, EventSource y iframe
     * de forma transparente.
     * 
     * 

     * Habilito todos los patrones de origen con 
     * durante el desarrollo. En producción, el API Gateway (BFF) se encarga de
     * validar CORS y origin, así que aquí podemos mantenerlo abierto sin riesgo.
     * 
     *
     * @param registry el registro de endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
