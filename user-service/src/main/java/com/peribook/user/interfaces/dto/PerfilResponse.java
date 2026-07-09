package com.peribook.user.interfaces.dto;

import java.time.LocalDate;

public record PerfilResponse(
        String id,
        String alias,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento
) {}
<!-- 2026-07-09 -->
