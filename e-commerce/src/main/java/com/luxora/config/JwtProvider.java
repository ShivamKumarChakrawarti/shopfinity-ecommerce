package com.luxora.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

@Component
public class JwtProvider {

    private final SecretKey key;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateToken(Authentication auth) {
        return Jwts.builder()
                .setSubject(auth.getName())
                .claim("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromJwtToken(String jwt) {

        if (jwt == null || jwt.isBlank()) {
            throw new IllegalArgumentException("JWT token is missing");
        }

        // Remove Bearer prefix if present
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7).trim();
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            String email = claims.getSubject(); // ✅ CORRECT

            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email (subject) is missing in JWT");
            }

            return email;

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }


    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
