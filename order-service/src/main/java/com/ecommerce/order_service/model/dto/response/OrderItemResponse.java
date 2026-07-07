package com.ecommerce.order_service.model.dto.response;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class OrderItemResponse {

    String productId;
    int quantity;
    BigDecimal unitPrice;
}
