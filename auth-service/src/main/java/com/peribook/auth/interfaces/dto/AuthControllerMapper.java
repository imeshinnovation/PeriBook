package com.peribook.auth.interfaces.dto;

import com.peribook.auth.application.LoginUseCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Mapper de MapStruct para convertir entre objetos del caso de uso y DTOs de la interfaz HTTP.
 * <p>
 * Decidí usar MapStruct en vez de escribir conversiones a mano porque:
 * <ol>
 *   <li>Elimina boilerplate repetitivo y propenso a errores</li>
 *   <li>Genera el código en tiempo de compilación, sin reflexión ni penalización en runtime</li>
 *   <li>Si los campos cambian, MapStruct falla en compilación, no en runtime</li>
 * </ol>
 * </p>
 * <p>
 * Configuré {@code componentModel = SPRING} para que MapStruct genere un bean
 * de Spring y pueda inyectarse automáticamente en el controller.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
@Mapper(componentModel = SPRING)
public interface AuthControllerMapper {

    /**
     * Convierte un {@link LoginUseCase.LoginResult} a {@link LoginResponse}.
     * <p>
     * Como los campos tienen exactamente los mismos nombres (token, userId, alias),
     * los mapeos explícitos con {@code @Mapping} son redundantes. Los dejé por
     * claridad documental — no está de más ser explícito cuando el mapper es
     * pequeño y se lee en contexto.
     * </p>
     *
     * @param result resultado exitoso del caso de uso de login
     * @return DTO de respuesta HTTP listo para serializar
     */
    @Mapping(source = "token", target = "token")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "alias", target = "alias")
    LoginResponse toResponse(LoginUseCase.LoginResult result);
}
