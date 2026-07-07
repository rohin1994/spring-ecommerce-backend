package com.ecommerce.order_service.model.dto.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CartResponse {

    List<CartItemResponse> items;
}
