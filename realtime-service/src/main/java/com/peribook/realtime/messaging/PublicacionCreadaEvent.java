package com.peribook.realtime.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO que refleja el contrato JSON del evento PublicacionCreada
 * emitido por post-service. Totalmente independiente.
 */
public record PublicacionCreadaEvent(
        UUID publicacionId,
        UUID autorId,
        String contenido,
        Instant creadaEn
) {}
