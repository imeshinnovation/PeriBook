package com.peribook.post.interfaces.dto;

import java.time.Instant;

/**
 * DTO de respuesta para representar una publicacion en la API REST.
 * 

 /> Los campos  y  son  en lugar de 
 * porque al serializar a JSON es mas conveniente que el cliente reciba
 *  en lugar de un objeto UUID. La
 * conversion la hace PostControllerMapper.
 * 

 * Use un  de Java 21 porque es inmutable, genera getters automaticamente
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
