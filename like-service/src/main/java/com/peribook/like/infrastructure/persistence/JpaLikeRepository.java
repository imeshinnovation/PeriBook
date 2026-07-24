package com.peribook.like.infrastructure.persistence;

import com.peribook.like.domain.Like;
import com.peribook.like.domain.LikeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador del repositorio de dominio (LikeRepository) usando Spring
 * Data JPA como mecanismo de persistencia.
 * 

 * Esta interfaz extiende tanto el puerto de dominio ()
 * como la interfaz tecnica de Spring Data ().
 * Decidi usar metodos  para implementar la traduccion entre el
 * dominio y la entidad JPA, evitando asi una clase separada de adaptador y
 * manteniendo el codigo compacto. Spring Data se encarga de generar las
 * implementaciones de  y
 *  a partir del nombre del metodo.
 * 
 *
 * @author Alexander Rubio Caceres
 */
@Repository
public interface JpaLikeRepository extends LikeRepository, JpaRepository<LikeEntity, UUID> {

    /**
     * Metodo derivado de Spring Data: genera automáticamente la query
     * .
     */
    Optional<LikeEntity> findByPublicacionIdAndUsuarioId(UUID publicacionId, UUID usuarioId);

    /**
     * Metodo derivado de Spring Data: genera un .
     */
    long countByPublicacionId(UUID publicacionId);

    @Override
    default Optional<Like> buscarPorPublicacionYUsuario(UUID publicacionId, UUID usuarioId) {
        // Traduzco la entidad JPA al agregado de dominio antes de devolverlo.
        return findByPublicacionIdAndUsuarioId(publicacionId, usuarioId).map(LikeEntity::toDomain);
    }

    @Override
    default long contarPorPublicacion(UUID publicacionId) {
        return countByPublicacionId(publicacionId);
    }

    @Override
    default Like save(Like like) {
        // Convierto del dominio a JPA, persisto con saveAndFlush (para obtener
        // el ID generado inmediatamente si aplica) y lo devuelvo como dominio.
        return this.saveAndFlush(LikeEntity.fromDomain(like)).toDomain();
    }
}
