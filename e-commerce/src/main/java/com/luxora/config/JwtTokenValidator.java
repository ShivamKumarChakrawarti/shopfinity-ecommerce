package com.luxora.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtTokenValidator(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();

            try {
                Claims claims = jwtProvider.parseClaims(token);

                String email = claims.getSubject();
                String authorities =
                        claims.get("authorities", String.class);

                var auths = AuthorityUtils
                        .commaSeparatedStringToAuthorityList(authorities);

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(email, null, auths)
                );

            } catch (Exception e) {
                throw new BadCredentialsException("Invalid JWT token");
            }
        }

        filterChain.doFilter(request, response);
    }
}


