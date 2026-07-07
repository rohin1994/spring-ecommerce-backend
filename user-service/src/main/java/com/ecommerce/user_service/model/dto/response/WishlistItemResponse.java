package com.ecommerce.user_service.model.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class WishlistItemResponse {

    String productId;
    Instant addedAt;
}
