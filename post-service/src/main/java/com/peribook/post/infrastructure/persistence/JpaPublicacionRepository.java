package com.peribook.post.infrastructure.persistence;

import com.peribook.post.domain.Publicacion;
import com.peribook.post.domain.PublicacionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPublicacionRepository
        extends PublicacionRepository, JpaRepository<PublicacionEntity, UUID> {

    @Override
    default Publicacion save(Publicacion p) {
        return this.saveAndFlush(PublicacionEntity.fromDomain(p)).toDomain();
    }

    @Override
    default Optional<Publicacion> buscarPorId(UUID id) {
        return this.findById(id).map(PublicacionEntity::toDomain);
    }

    @Override
    default List<Publicacion> listarRecientes(int limite) {
        return this.findAllByOrderByCreadaEnDesc()
                .stream()
                .limit(limite)
                .map(PublicacionEntity::toDomain)
                .toList();
    }

    List<PublicacionEntity> findAllByOrderByCreadaEnDesc();
}
<!-- 2026-07-09 -->
