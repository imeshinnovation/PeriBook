package com.peribook.like.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida (driven port) para la persistencia de {@link Like}.
 * <p>
 * Definí esta interfaz en la capa de dominio para que el núcleo no dependa de
 * JPA, Spring Data ni ninguna tecnología en concreto. La implementación concreta
 * vive en infraestructura y se inyecta en tiempo de ejecución. Esto me permite
 * cambiar de base de datos o de estrategia de persistencia sin tocar ni una línea
 * del caso de uso.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
public interface LikeRepository {
    /**
     * Persiste un Like (INSERT) y devuelve la entidad resultante con los valores
     * ya normalizados por el almacén.
     */
    Like save(Like like);

    /**
     * Búsqueda por clave natural {@code (publicacionId, usuarioId)}. La uso en el
     * caso de uso para implementar idempotencia: si ya existe, no duplico el like.
     */
    Optional<Like> buscarPorPublicacionYUsuario(UUID publicacionId, UUID usuarioId);

    /**
     * Total de likes de una publicación. Lo usa el controlador para responder
     * al endpoint de conteo.
     */
    long contarPorPublicacion(UUID publicacionId);
}
