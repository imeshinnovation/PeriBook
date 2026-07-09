package com.peribook.like.infrastructure.persistence;

import com.peribook.like.domain.Like;
import com.peribook.like.domain.LikeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaLikeRepository extends LikeRepository, JpaRepository<LikeEntity, UUID> {

    Optional<LikeEntity> findByPublicacionIdAndUsuarioId(UUID publicacionId, UUID usuarioId);

    long countByPublicacionId(UUID publicacionId);

    @Override
    default Optional<Like> buscarPorPublicacionYUsuario(UUID publicacionId, UUID usuarioId) {
        return findByPublicacionIdAndUsuarioId(publicacionId, usuarioId).map(LikeEntity::toDomain);
    }

    @Override
    default long contarPorPublicacion(UUID publicacionId) {
        return countByPublicacionId(publicacionId);
    }

    @Override
    default Like save(Like like) {
        return this.saveAndFlush(LikeEntity.fromDomain(like)).toDomain();
    }
}
<!-- 2026-07-09 -->
