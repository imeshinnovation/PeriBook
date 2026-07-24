package com.peribook.post.interfaces.dto;

import com.peribook.post.domain.Publicacion;
import org.mapstruct.Mapper;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Mapper de MapStruct para convertir entidades de dominio a DTOs de respuesta.
 * <p>
 * Decidi usar MapStruct en lugar de escribir manualmente los mapeos porque:
 * <ol>
 *   <li>Genera codigo en tiempo de compilacion, no en ejecucion (sin reflection)</li>
 *   <li>Es mas rapido que manualmente escribir los mismos metodos</li>
 *   <li>Si los campos cambian, MapStruct falla en compilacion, no en runtime</li>
 * </ol>
 * <p>
 * Use un metodo {@code default} para {@link #toResponse(Publicacion)} porque
 * la conversion entre {@code UUID} y {@code String} no es trivial para MapStruct
 * ya que ambos son tipos diferentes. Prefiero definirla explicitamente a tener
 * que configurar un mapeo personalizado con anotaciones.
 *
 * @author Alexander Rubio Caceres
 */
@Mapper(componentModel = SPRING)
public interface PostControllerMapper {

    /**
     * Convierte una entidad {@link Publicacion} de dominio a un DTO
     * {@link PublicacionResponse} listo para serializar a JSON.
     * <p>
     * Los campos {@code id} y {@code autorId} se convierten a String para que
     * la serializacion JSON sea mas amigable (los UUID como string son mas faciles
     * de consumir desde JavaScript que como objetos binarios).
     *
     * @param p la entidad de dominio
     * @return el DTO de respuesta
     */
    default PublicacionResponse toResponse(Publicacion p) {
        return new PublicacionResponse(
                p.id().toString(), p.autorId().toString(),
                p.contenido(), p.creadaEn());
    }
}
