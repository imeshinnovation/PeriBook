package com.peribook.post.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearPublicacionRequest(
        @NotBlank @Size(max = 500) String contenido
) {}
<!-- 2026-07-09 -->
