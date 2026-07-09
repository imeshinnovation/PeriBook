package com.peribook.auth.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
        String issuer,
        Duration expiration,
        RsaKeys rsa
) {
    public record RsaKeys(
            String publicKeyPath,
            String privateKeyPath
    ) {}
}
<!-- 2026-07-09 -->
