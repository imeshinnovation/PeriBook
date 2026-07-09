package com.peribook.realtime.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO que refleja el contrato JSON del evento LikeRegistrado
 * emitido por like-service. Totalmente independiente.
 */
public record LikeRegistradoEvent(
        UUID likeId,
        UUID publicacionId,
        UUID usuarioId,
        Instant creadoEn
) {}
