package com.ecommerce.auth_service.security;

import com.ecommerce.common.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String expectedAudience = resolveExpectedAudience(request.getRequestURI());
                JwtClaims claims = jwtService.validateToken(token, expectedAudience);
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(claims));
            } catch (JwtValidationException ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveExpectedAudience(String path) {
        if (path.startsWith("/api/v1/admin")) {
            return SecurityConstants.AUD_ADMIN;
        }
        return SecurityConstants.AUD_ECOMMERCE;
    }
}
