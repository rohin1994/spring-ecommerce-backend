package com.ecommerce.user_service.model.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserProfileResponse {

    String userId;
    String name;
    String email;
    String phone;
}
