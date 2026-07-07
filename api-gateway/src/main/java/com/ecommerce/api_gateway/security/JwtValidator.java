package com.ecommerce.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtValidator {

    private final SecretKey secretKey;

    public JwtValidator(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean hasAudience(Claims claims, String expectedAudience) {
        Object audience = claims.get("aud");
        if (audience instanceof String value) {
            return expectedAudience.equals(value);
        }
        if (audience instanceof List<?> values) {
            return values.stream().anyMatch(expectedAudience::equals);
        }
        return false;
    }
}
