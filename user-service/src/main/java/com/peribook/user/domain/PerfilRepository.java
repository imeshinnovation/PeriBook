package com.peribook.user.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de repositorio en la capa de dominio (hexagonal/DDD).
 * 

 * Esta interfaz pertenece al dominio porque el caso de uso 
 * necesita recuperar y persistir agregados  sin conocer los detalles
 * de infraestructura (JPA, MongoDB, cache, lo que sea). La implementación concreta
 * vive en  y se inyecta en tiempo de ejecución.
 * 
 * 

 * Decidí mantener el interfaz minimalista a propósito: solo las operaciones que
 * el dominio necesita realmente. Cualquier consulta compleja (búsqueda por alias,
 * listado paginado) pertenecería a un  separado si
 * hiciera falta — no voy a contaminar el puerto del dominio con métodos de
 * proyección que solo la UI necesita.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
public interface PerfilRepository {
    /**
     * Busca un perfil por su ID.
     *
     * @param id UUID del perfil
     * @return Optional con el Perfil si existe, vacío si no
     */
    Optional<Perfil> buscarPorId(UUID id);

    /**
     * Persiste un perfil (insert o update según si el ID ya existe).
     *
     * @param perfil Agregado Perfil a guardar
     * @return Perfil persistido (con estado actualizado si el repositorio lo modificó)
     */
    Perfil save(Perfil perfil);
}
