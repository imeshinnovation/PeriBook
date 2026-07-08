package com.peribook.auth.interfaces.dto;

public record LoginResponse(
        String token,
        String userId,
        String alias
) {}
