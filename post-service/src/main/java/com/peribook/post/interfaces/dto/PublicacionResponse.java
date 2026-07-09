package com.peribook.post.interfaces.dto;

import java.time.Instant;

public record PublicacionResponse(
        String id,
        String autorId,
        String contenido,
        Instant creadaEn
) {}
