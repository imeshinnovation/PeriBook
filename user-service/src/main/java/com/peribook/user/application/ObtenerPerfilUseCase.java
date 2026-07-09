package com.peribook.user.application;

import com.peribook.user.domain.Perfil;
import com.peribook.user.domain.PerfilRepository;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ObtenerPerfilUseCase {

    private final PerfilRepository perfilRepository;

    public ObtenerPerfilUseCase(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    public Perfil obtener(UUID id) {
        return perfilRepository.buscarPorId(id)
                .orElseThrow(() -> new PerfilNoEncontradoException(id));
    }
}
<!-- 2026-07-09 -->
