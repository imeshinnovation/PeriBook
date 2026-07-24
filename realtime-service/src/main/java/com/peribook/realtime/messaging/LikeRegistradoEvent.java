package com.peribook.realtime.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) que modela el evento de dominio "LikeRegistrado".
 * <p>
 * Este record refleja el contrato JSON del evento que publica
 * <strong>like-service</strong> en RabbitMQ. Decidí usar un DTO independiente
 * en cada servicio consumidor en lugar de compartir un JAR común porque quiero
 * evitar el acoplamiento entre servicios a nivel de artefacto de compilación:
 * cada servicio define su propia versión del evento y es responsabilidad del
 * exchange (y del contrato JSON) garantizar la compatibilidad.
 * </p>
 * <p>
 * Uso un <strong>{@code record}</strong> de Java 21 porque es inmutable por
 * diseño, genera automáticamente los accesores, {@code equals()},
 * {@code hashCode()} y {@code toString()}. Justo lo que necesito para un DTO
 * que solo transporta datos sin comportamiento.
 * </p>
 *
 * @author Alexander Rubio Caceres
 *
 * @param likeId        identificador único del like (generado por like-service)
 * @param publicacionId identificador de la publicación que recibió el like
 * @param usuarioId     identificador del usuario que dio el like
 * @param creadoEn      instante en que se registró el like (UTC)
 */
public record LikeRegistradoEvent(
        UUID likeId,
        UUID publicacionId,
        UUID usuarioId,
        Instant creadoEn
) {}
