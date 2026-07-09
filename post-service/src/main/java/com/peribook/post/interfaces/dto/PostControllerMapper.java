package com.peribook.post.interfaces.dto;

import com.peribook.post.domain.Publicacion;
import org.mapstruct.Mapper;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PostControllerMapper {

    default PublicacionResponse toResponse(Publicacion p) {
        return new PublicacionResponse(
                p.id().toString(), p.autorId().toString(),
                p.contenido(), p.creadaEn());
    }
}
<!-- 2026-07-09 -->
