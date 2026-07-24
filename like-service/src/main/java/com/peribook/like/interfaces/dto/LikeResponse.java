package com.peribook.like.interfaces.dto;

/**
 * DTO de respuesta para el endpoint REST de like.
 * <p>
 * Lo defini como un {@code record} de Java porque es inmutable, no necesita
 * logica propia y solo transporta datos entre el controlador y el cliente HTTP.
 * El campo {@code totalLikes} se deja en 0 desde el like-service (no tiene el
 * contexto completo); el BFF lo pobla en una fase posterior agregando la
 * informacion del contador.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
public record LikeResponse(String id, String publicacionId, long totalLikes, boolean esNuevo) {}
