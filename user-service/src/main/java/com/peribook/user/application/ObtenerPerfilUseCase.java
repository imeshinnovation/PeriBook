package com.peribook.user.application;

import com.peribook.user.domain.Perfil;
import com.peribook.user.domain.PerfilRepository;

import java.util.UUID;

import org.springframework.stereotype.Service;

/**
 * Caso de uso: "Obtener perfil por ID".
 * 

 * Este caso de uso es intencionalmente simple — sigue el principio de
 * Responsabilidad Única: recibe un ID, delega la búsqueda al repositorio,
 * y si no encuentra el perfil lanza una excepción de dominio que el controlador
 * sabe cómo traducir a HTTP 404. Punto.
 * 
 * 

 * Marcado con  porque es un servicio de aplicación (orquestador),
 * no un servicio de dominio. No contiene lógica de negocio — solo coordina
 * la interacción entre el mundo exterior (controlador) y el dominio.
 * Si en el futuro este caso de uso necesitara, por ejemplo, invalidar una cache
 * o publicar un evento, este es el lugar donde agregaría esa coordinación.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
@Service
public class ObtenerPerfilUseCase {

    private final PerfilRepository perfilRepository;

    /**
     * Inyección por constructor — mi estilo preferido porque hace explícitas
     * las dependencias, facilita los tests unitarios (sin reflexión ni mocking
     * mágico) y deja los campos como .
     */
    public ObtenerPerfilUseCase(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    /**
     * Ejecuta el caso de uso: busca un perfil por UUID.
     *
     * @param id Identificador único del perfil
     * @return Perfil encontrado
     * @throws PerfilNoEncontradoException si no existe un perfil con ese ID
     */
    public Perfil obtener(UUID id) {
        return perfilRepository.buscarPorId(id)
                .orElseThrow(() -> new PerfilNoEncontradoException(id));
    }
}
