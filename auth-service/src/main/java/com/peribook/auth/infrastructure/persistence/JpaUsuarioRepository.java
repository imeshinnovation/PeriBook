package com.peribook.auth.infrastructure.persistence;

import com.peribook.auth.domain.Email;
import com.peribook.auth.domain.Usuario;
import com.peribook.auth.domain.UsuarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUsuarioRepository extends UsuarioRepository, JpaRepository<UsuarioEntity, UUID> {

    Optional<UsuarioEntity> findByEmailIgnoreCase(String email);

    @Override
    default Optional<Usuario> findByEmail(Email email) {
        return findByEmailIgnoreCase(email.value())
                .map(UsuarioEntity::toDomain);
    }

    @Override
    default Usuario save(Usuario usuario) {
        UsuarioEntity entity = UsuarioEntity.fromDomain(usuario);
        UsuarioEntity saved = this.saveAndFlush(entity); // Llama al save de JpaRepository
        return saved.toDomain();
    }
}
<!-- 2026-07-09 -->
