package com.peribook.auth.interfaces.dto;

import com.peribook.auth.application.LoginUseCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface AuthControllerMapper {

    @Mapping(source = "token", target = "token")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "alias", target = "alias")
    LoginResponse toResponse(LoginUseCase.LoginResult result);
}
<!-- 2026-07-09 -->
