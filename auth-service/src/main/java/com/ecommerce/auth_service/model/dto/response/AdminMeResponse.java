package com.ecommerce.auth_service.model.dto.response;

import java.util.List;

public record AdminMeResponse(
        String id,
        String email,
        String name,
        List<String> roles,
        List<String> permissions
) {
}
