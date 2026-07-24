package com.peribook.bff.domain;

/**
 * Representa un item del feed enriquecido que devuelve el BFF al frontend.
 * 

 * Decidi usar un  de Java 21 porque es inmutable por definicion,
 * tiene equals/hashCode/toString gratis, y no necesita JPA ni anotaciones. Esto
 * es un DTO de dominio puro: no hay comportamiento, solo datos que viajan desde
 * la capa de infraestructura hasta el controlador. Al ser un record, no hay riesgo
 * de que alguien mute los campos accidentalmente.
 * 
 * 

 * Los campos reflejan justo lo que necesita la SPA: el texto de la publicacion,
 * quien la escribio, cuando, el alias legible del autor, y cuantos likes tiene.
 * No expongo IDs internos de base de datos ni relaciones de agregacion — eso es
 * responsabilidad de los servicios internos, no del BFF.
 * 
 *
 * @param publicacionId identificador unico de la publicacion
 * @param autorId       identificador del autor
 * @param contenido     texto o contenido de la publicacion
 * @param creadaEn      timestamp de creacion
 * @param autorAlias    alias del autor (resuelto desde user-service)
 * @param totalLikes    cantidad de likes (resuelto desde like-service)
 *
 * @author Alexander Rubio Caceres
 */
public record FeedItem(
        String publicacionId,
        String autorId,
        String contenido,
        String creadaEn,
        String autorAlias,
        long totalLikes
) {}
