package com.ecommerce.user_service.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {

    @Size(max = 120)
    private String name;

    @Size(max = 255)
    private String email;

    @Size(max = 30)
    private String phone;
}
