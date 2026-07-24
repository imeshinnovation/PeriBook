package com.peribook.bff.infrastructure;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Configuracion global del cliente HTTP reactivo (WebClient).
 * <p>
 * Uso {@code proxyBeanMethods = false} para que Spring no genere proxies CGLIB
 * sobre esta configuracion, ya que solo expone un bean y no necesita
 * interdependencias con otros @Bean.
 * </p>
 * <p>
 * Decidi configurar los timeouts a nivel de HttpClient en vez de en cada llamada
 * porque son valores uniformes para todos los clientes del BFF. Si algun cliente
 * necesitara timeouts distintos, se podria sobreescribir en su propia
 * configuracion, pero prefiero empezar con un valor sensato y evitar la
 * repeticion.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@Configuration(proxyBeanMethods = false)
public class WebClientConfig {

    /**
     * Fabrica de {@link WebClient.Builder} preconfigurada con timeouts.
     * <p>
     * Los valores son de 5 segundos para connect, read y write. En un entorno
     * de microservicios en Docker Swarm, 5s es suficiente para detectar que un
     * servicio esta caido sin que la experiencia del usuario se degrade demasiado.
     * Si un servicio no responde en ese tiempo, el BFF corta la llamada y aplica
     * el fallback correspondiente (ver LikeClient.onErrorReturn).
     * </p>
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                // Timeout de conexion TCP: si el servicio no esta alcanzable, no
                // esperamos mas de 5 segundos.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // Timeout para recibir la respuesta completa.
                .responseTimeout(Duration.ofSeconds(5))
                // Timeouts de lectura/escritura a nivel de canal Netty, por si el
                // servicio deja de enviar datos a medio camino.
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5))
                                .addHandlerLast(new WriteTimeoutHandler(5)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}
