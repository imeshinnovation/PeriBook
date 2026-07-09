package com.peribook.auth.infrastructure.security;

import com.peribook.auth.application.JwtService;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;

@Service
public class RsaJwtService implements JwtService {

    private final JwtConfig jwtConfig;
    private final PrivateKey privateKey;

    public RsaJwtService(JwtConfig jwtConfig, PrivateKey privateKey) {
        this.jwtConfig = jwtConfig;
        this.privateKey = privateKey;
    }

    @Override
    public String generate(String userId, String email) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtConfig.expiration());

        return Jwts.builder()
                .issuer(jwtConfig.issuer())
                .subject(email)
                .claim("userId", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }
}
<!-- 2026-07-09 -->
