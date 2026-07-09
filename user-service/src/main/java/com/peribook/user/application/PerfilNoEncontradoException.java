package com.peribook.user.application;

import java.util.UUID;

public class PerfilNoEncontradoException extends RuntimeException {
    public PerfilNoEncontradoException(UUID id) {
        super("Perfil no encontrado: " + id);
    }
}
