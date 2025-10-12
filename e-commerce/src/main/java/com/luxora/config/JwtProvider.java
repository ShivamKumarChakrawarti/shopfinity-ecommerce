package com.luxora.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

@Component
public class JwtProvider {

    private final SecretKey key;

    public JwtProvider() {
        String secretKey = "your-very-strong-secret-key-that-is-at-least-32-characters-long";
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes()); // ✅ Ensures the key is properly initialized
    }

    public String generateToken(Authentication auth) {
        String roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Log for debugging purposes (optional)
        System.out.println("Generating JWT for user: " + auth.getName());

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours expiry
                .claim("email", auth.getName())  // Ensuring email is added as a claim
                .claim("authorities", roles)
                .signWith(key, SignatureAlgorithm.HS256) // ✅ Specify the signing algorithm
                .compact();
    }

    public String getEmailFromJwtToken(String jwt) {
        // Check if the JWT is prefixed with "Bearer " and remove it
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7); // Remove "Bearer " prefix
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            String email = claims.get("email", String.class);
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email claim is missing in JWT");
            }

            return email;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage(), e);
        }
    }
}
