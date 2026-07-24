package com.peribook.post.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de dominio: se emite cuando se crea una nueva publicacion.
 * 

 * Elegi un  de Java 21 porque es inmutable por naturaleza, tiene
 * equals/hashcode/toString generados automaticamente y expresa de forma concisa
 * que esto es solo un transporte de datos (un DTO de dominio, por llamarlo de algun modo).
 * 

 * Este evento viaja por RabbitMQ en formato JSON hacia otros servicios (feed-service,
 * notification-service, etc.). Cada servicio consumidor define su propia interpretacion
 * del evento — no compartimos clases compiladas entre microservicios. Eso evita
 * acoplamiento de despliegue y permite que cada equipo evolucione a su ritmo.
 *
 * @author Alexander Rubio Caceres
 */
public record PublicacionCreada(
        UUID publicacionId,
        UUID autorId,
        String contenido,
        Instant creadaEn
) {
    /**
     * Construye el evento a partir de una entidad Publicacion.
     * 

     * Es un metodo factory que extrae los valores relevantes del agregado raiz para
     * armar el mensaje que otros servicios consumiran. Decidi que estuviera aqui en
     * lugar de en el caso de uso para mantener la logica de conversion cerca del
     * propio evento.
     *
     * @param p la entidad Publicacion ya persistida
     * @return un nuevo evento con los datos de la publicacion
     */
    public static PublicacionCreada desde(Publicacion p) {
        return new PublicacionCreada(p.id(), p.autorId(), p.contenido(), p.creadaEn());
    }
}
