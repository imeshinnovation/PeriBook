package com.peribook.user.application;

import java.util.UUID;

/**
 * Excepción lanzada cuando se solicita un perfil que no existe en el repositorio.
 * 

 * Extiende RuntimeException (unchecked) porque no quiero forzar al
 * llamante a declararla en su firma — en Spring MVC las excepciones no verificadas
 * se manejan centralizadamente con . Si usara excepciones
 * checked, cada capa intermedia tendría que declararlas o atraparlas, agregando
 * ruido sin beneficio real en una arquitectura con manejo centralizado de errores.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
public class PerfilNoEncontradoException extends RuntimeException {
    /**
     * Construye la excepción con un mensaje descriptivo que incluye el ID.
     *
     * @param id UUID del perfil que no se encontró
     */
    public PerfilNoEncontradoException(UUID id) {
        super("Perfil no encontrado: " + id);
    }
}
