package com.github.giga_chill.gigachill.security;

import com.github.giga_chill.gigachill.properties.JwtProperties;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final JwtProperties properties;

    private final Key key = Jwts.SIG.HS256.key().build();

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    public String generateToken(String username) {
        Date expiration = Date.from(Instant.now().plus(properties.getExpiration()));

        return Jwts.builder()
                .subject(username)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token);
    }

    public String extractUsername(String token) {
        return parseClaims(token).getPayload().getSubject();
    }
}