package com.ecommerce.order_service.model.dto.response;

import com.ecommerce.order_service.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Value
@Builder
public class OrderResponse {

    String id;
    OrderStatus status;
    BigDecimal totalAmount;
    Instant createdAt;
    List<OrderItemResponse> items;
}
