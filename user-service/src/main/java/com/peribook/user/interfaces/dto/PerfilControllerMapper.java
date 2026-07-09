package com.peribook.user.interfaces.dto;

import com.peribook.user.domain.Perfil;
import org.mapstruct.Mapper;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PerfilControllerMapper {

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
