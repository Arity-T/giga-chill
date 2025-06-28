package com.github.giga_chill.gigachill.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private final Key key = Jwts.SIG.HS256.key().build();
    private final long expirationMs = 24 * 60 * 60 * 1000; // 24 часа

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
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