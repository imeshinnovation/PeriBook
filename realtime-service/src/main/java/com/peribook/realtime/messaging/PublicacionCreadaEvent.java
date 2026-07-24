package com.peribook.realtime.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) que modela el evento de dominio "PublicacionCreada".
 * 

 * Este record refleja el contrato JSON del evento que publica
 * post-service en RabbitMQ cuando un usuario crea una nueva
 * publicación. Al igual que en LikeRegistradoEvent, prefiero mantener
 * este DTO duplicado en el servicio consumidor en lugar de compartir un JAR
 * común, para evitar dependencias de compilación entre servicios.
 * 
 * 

 * El campo  se incluye completo para que el feed en tiempo
 * real pueda mostrar la publicación inmediatamente sin tener que hacer una
 * llamada REST a post-service. Esto reduce la latencia percibida por el
 * usuario y descarga al post-service de peticiones adicionales.
 * 
 *
 * @author Alexander Rubio Caceres
 *
 * @param publicacionId identificador único de la publicación
 * @param autorId       identificador del usuario que creó la publicación
 * @param contenido     texto completo de la publicación (se envía en el evento
 *                      para evitar llamadas REST adicionales al post-service)
 * @param creadaEn      instante en que se creó la publicación (UTC)
 */
public record PublicacionCreadaEvent(
        UUID publicacionId,
        UUID autorId,
        String contenido,
        Instant creadaEn
) {}
