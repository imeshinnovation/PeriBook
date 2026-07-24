package com.peribook.user.interfaces.dto;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) que representa la respuesta pública de un perfil.
 * <p>
 * Usar un {@code record} para los DTOs es una decisión deliberada: son inmutables,
 * generan equals/hashCode/toString automáticamente, y su sintaxis compacta hace
 * que la estructura del contrato API sea evidente de un vistazo.
 * </p>
 * <p>
 * Este DTO no expone toda la información del agregado {@code Perfil}. Es una
 * decisión consciente de seguridad y diseño: el API pública solo devuelve lo
 * que el frontend necesita, nada más.
 * </p>
 *
 * @param id              UUID del perfil como String (evita inconsistencias de serialización)
 * @param alias           Seudónimo público del usuario
 * @param nombres         Nombre(s) del usuario
 * @param apellidos       Apellido(s) del usuario
 * @param fechaNacimiento Fecha de nacimiento (opcional, null si no se proporcionó)
 *
 * @author Alexander Rubio Cáceres
 */
public record PerfilResponse(
        String id,
        String alias,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento
) {}
