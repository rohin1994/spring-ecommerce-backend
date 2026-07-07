package com.ecommerce.auth_service.model.dto.response;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        List<String> roles,
        List<String> permissions
) {
    public static AuthResponse of(String accessToken, String refreshToken, long expiresInSeconds) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInSeconds, null, null);
    }

    public static AuthResponse of(String accessToken, String refreshToken, long expiresInSeconds,
                                  List<String> roles, List<String> permissions) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInSeconds, roles, permissions);
    }
}
