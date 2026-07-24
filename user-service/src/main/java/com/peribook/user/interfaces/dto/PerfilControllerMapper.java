package com.peribook.user.interfaces.dto;

import com.peribook.user.domain.Perfil;
import org.mapstruct.Mapper;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Mapper entre el agregado de dominio Perfil y el DTO de respuesta
 * PerfilResponse.
 * 

 * Declarado como interfaz MapStruct para generar la implementación automáticamente
 * en tiempo de compilación (sin reflexión, sin errores en runtime). Sin embargo,
 * en este caso particular uso un método  porque el mapeo es tan
 * simple que MapStruct no necesita generar nada — la implementación live en la
 * interfaz es más legible que la anotación  para cada campo.
 * 
 * 

 * Notar que no expongo el  ni el  en la respuesta.
 * El  se omite porque el cliente ya lo conoce (viene en el JWT),
 * y el  se omite por privacidad — el endpoint público de perfil no
 * debería filtrar direcciones de email a menos que el usuario lo autorice.
 * Si en el futuro el frontend necesita el email, se agrega un campo específico
 * con su propio control de acceso.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
@Mapper(componentModel = SPRING)
public interface PerfilControllerMapper {

    /**
     * Convierte un Perfil en un PerfilResponse para la API REST.
     * El campo  se pasa como String para evitar que el JSON serialice
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
