package com.peribook.bff.domain;

/**
 * Representa un ítem del feed enriquecido: publicación + alias del autor + contador de likes.
 * Es un DTO de dominio — no tiene comportamiento, solo transporta datos agregados.
 */
public record FeedItem(
        String publicacionId,
        String autorId,
        String contenido,
        String creadaEn,
        String autorAlias,
        long totalLikes
) {}
<!-- 2026-07-09 -->
