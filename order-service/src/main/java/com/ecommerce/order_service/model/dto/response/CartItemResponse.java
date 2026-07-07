package com.ecommerce.order_service.model.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CartItemResponse {

    String productId;
    int quantity;
}
