package com.peribook.user.domain;

import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository {
    Optional<Perfil> buscarPorId(UUID id);
    Perfil save(Perfil perfil);
}
