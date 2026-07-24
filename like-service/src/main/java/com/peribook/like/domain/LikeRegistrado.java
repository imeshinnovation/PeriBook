package com.peribook.like.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de dominio: "un like fue registrado".
 * <p>
 * Lo modele como un {@code record} de Java porque es inmutable por diseño,
 * encapsula todos los datos relevantes del suceso y no necesita comportamiento.
 * Este evento lo publica el caso de uso {@code DarLikeUseCase} hacia RabbitMQ
 * para que otros servicios (feed, realtime, notificaciones) reaccionen.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
public record LikeRegistrado(
        UUID likeId,
        UUID publicacionId,
        UUID usuarioId,
        Instant creadoEn
) {
    /**
     * Convierte el agregado raíz {@link Like} en el evento de dominio.
     * Decidí poner esta lógica aquí y no en el caso de uso para que la
     * transformación esté junto a la definición del evento y sea fácil de
     * mantener si los campos cambian.
     */
    public static LikeRegistrado desde(Like like) {
        return new LikeRegistrado(like.id(), like.publicacionId(), like.usuarioId(), like.creadoEn());
    }
}
