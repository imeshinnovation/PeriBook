package com.peribook.auth.domain;

import java.util.Optional;

/**
 * Puerto de persistencia para el agregado Usuario.
 * La implementación concreta vive en infrastructure/persistence.
 */
public interface UsuarioRepository {

    Optional<Usuario> findByEmail(Email email);

    Usuario save(Usuario usuario);
}
