package com.ecommerce.auth_service.security;

import java.util.List;

public record JwtClaims(
        String subject,
        String audience,
        String email,
        List<String> roles,
        List<String> permissions
) {
}
