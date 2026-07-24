package com.peribook.post.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de repositorio para el agregado Publicacion.
 * 

 * En la terminologia de Hexagonal Architecture, esto es un "puerto de salida"
 * (outbound port). La capa de dominio define el contrato para persistir y recuperar
 * publicaciones, pero no tiene idea de si la implementacion usa JPA, MongoDB, una
 * API REST externa o simplemente una lista en memoria.
 * 

 * Decidi mantener el minimo de operaciones necesario: guardar, buscar por ID y
 * listar recientes. No incluyo actualizacion ni borrado porque en esta version del
 * dominio las publicaciones no se editan ni se eliminan — si en el futuro se
 * necesitan, se agregarian aqui como nuevas operaciones del contrato.
 *
 * @author Alexander Rubio Caceres
 */
public interface PublicacionRepository {

    /**
     * Persiste una publicacion (nueva o existente).
     * En la implementacion con JPA, esto se traduce en un merge/insert dependiendo
     * de si el ID ya existe en la base de datos.
     *
     * @param publicacion la entidad Publicacion a guardar
     * @return la entidad persistida (con posibles modificaciones de la BD)
     */
    Publicacion save(Publicacion publicacion);

    /**
     * Busca una publicacion por su identificador unico.
     *
     * @param id UUID de la publicacion
     * @return un Optional con la publicacion si existe, o vacio si no se encuentra
     */
    Optional<Publicacion> buscarPorId(UUID id);

    /**
     * Lista las publicaciones mas recientes, ordenadas por fecha de creacion descendente.
     *
     * @param limite cantidad maxima de resultados a devolver
     * @return lista de publicaciones (vacia si no hay ninguna)
     */
    List<Publicacion> listarRecientes(int limite);
}
