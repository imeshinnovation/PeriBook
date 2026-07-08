package com.peribook.user.infrastructure.persistence;

import com.peribook.user.domain.Perfil;
import com.peribook.user.domain.PerfilRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPerfilRepository extends PerfilRepository, JpaRepository<PerfilEntity, UUID> {

    @Override
    default Optional<Perfil> buscarPorId(UUID id) {
        return this.findById(id).map(PerfilEntity::toDomain);
    }

    @Override
    default Perfil save(Perfil perfil) {
        return this.saveAndFlush(PerfilEntity.fromDomain(perfil)).toDomain();
    }
}
