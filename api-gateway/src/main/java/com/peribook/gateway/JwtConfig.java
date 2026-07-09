package com.peribook.gateway;

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
