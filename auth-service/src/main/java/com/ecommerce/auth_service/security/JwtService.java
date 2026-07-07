package com.ecommerce.auth_service.security;

import com.ecommerce.common.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey signingKey;

    public JwtService(com.ecommerce.auth_service.config.JwtProperties properties) {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createCustomerAccessToken(String subject, String email, long expirationSeconds) {
        return buildToken(subject, SecurityConstants.AUD_ECOMMERCE, email, null, null, expirationSeconds);
    }

    public String createAdminAccessToken(String subject, String email, List<String> roles,
                                         List<String> permissions, long expirationSeconds) {
        return buildToken(subject, SecurityConstants.AUD_ADMIN, email, roles, permissions, expirationSeconds);
    }

    private String buildToken(String subject, String audience, String email, List<String> roles,
                              List<String> permissions, long expirationSeconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationSeconds * 1000);

        var builder = Jwts.builder()
                .subject(subject)
                .audience().add(audience).and()
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiry);

        if (roles != null) {
            builder.claim("roles", roles);
        }
        if (permissions != null) {
            builder.claim("permissions", permissions);
        }

        return builder.signWith(signingKey).compact();
    }

    public JwtClaims validateToken(String token, String expectedAudience) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireAudience(expectedAudience)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new JwtClaims(
                    claims.getSubject(),
                    claims.getAudience().iterator().next(),
                    claims.get("email", String.class),
                    getStringList(claims, "roles"),
                    getStringList(claims, "permissions")
            );
        } catch (JwtException | IllegalArgumentException ex) {
            throw new JwtValidationException("Invalid or expired token", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getStringList(Claims claims, String claimName) {
        Object value = claims.get(claimName);
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return Collections.emptyList();
    }
}
