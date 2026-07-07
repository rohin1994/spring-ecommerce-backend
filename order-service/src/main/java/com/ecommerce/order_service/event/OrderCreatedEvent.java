package com.ecommerce.order_service.event;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Value
@Builder
public class OrderCreatedEvent {

    String orderId;
    String userId;
    BigDecimal totalAmount;
    Instant createdAt;
    List<OrderLine> items;

    @Value
    @Builder
    public static class OrderLine {
        String productId;
        int quantity;
        BigDecimal unitPrice;
    }
}
