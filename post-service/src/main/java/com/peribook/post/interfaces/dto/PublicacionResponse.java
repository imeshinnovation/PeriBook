package com.peribook.post.interfaces.dto;

import java.time.Instant;

/**
 * DTO de respuesta para representar una publicacion en la API REST.
 * <p>
 /> Los campos {@code id} y {@code autorId} son {@code String} en lugar de {@code UUID}
 * porque al serializar a JSON es mas conveniente que el cliente reciba
 * {@code "550e8400-e29b-41d4-a716-446655440000"} en lugar de un objeto UUID. La
 * conversion la hace {@link PostControllerMapper}.
 * <p>
 * Use un {@code record} de Java 21 porque es inmutable, genera getters automaticamente
 * y se serializa correctamente con Jackson sin necesidad de anotaciones adicionales.
 *
 * @author Alexander Rubio Caceres
 */
public record PublicacionResponse(
        String id,
        String autorId,
        String contenido,
        Instant creadaEn
) {}
