package com.peribook.user.interfaces.dto;

import com.peribook.user.domain.Perfil;
import org.mapstruct.Mapper;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Mapper entre el agregado de dominio {@link Perfil} y el DTO de respuesta
 * {@link PerfilResponse}.
 * <p>
 * Declarado como interfaz MapStruct para generar la implementación automáticamente
 * en tiempo de compilación (sin reflexión, sin errores en runtime). Sin embargo,
 * en este caso particular uso un método {@code default} porque el mapeo es tan
 * simple que MapStruct no necesita generar nada — la implementación live en la
 * interfaz es más legible que la anotación {@code @Mapping} para cada campo.
 * </p>
 * <p>
 * Notar que no expongo el {@code usuarioId} ni el {@code email} en la respuesta.
 * El {@code usuarioId} se omite porque el cliente ya lo conoce (viene en el JWT),
 * y el {@code email} se omite por privacidad — el endpoint público de perfil no
 * debería filtrar direcciones de email a menos que el usuario lo autorice.
 * Si en el futuro el frontend necesita el email, se agrega un campo específico
 * con su propio control de acceso.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
@Mapper(componentModel = SPRING)
public interface PerfilControllerMapper {

    /**
     * Convierte un {@link Perfil} en un {@link PerfilResponse} para la API REST.
     * El campo {@code id} se pasa como String para evitar que el JSON serialice
     * el UUID con guiones de forma inconsistente entre servicios.
     *
     * @param perfil Agregado de dominio (nunca null)
     * @return DTO listo para serializar a JSON
     */
    default PerfilResponse toResponse(Perfil perfil) {
        return new PerfilResponse(
                perfil.id().toString(),
                perfil.alias(),
                perfil.nombres(),
                perfil.apellidos(),
                perfil.fechaNacimiento()
        );
    }
}
