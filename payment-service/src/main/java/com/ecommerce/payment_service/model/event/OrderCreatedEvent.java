package com.ecommerce.payment_service.model.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderCreatedEvent(
        String orderId,
        String userId,
        BigDecimal totalAmount,
        Instant createdAt,
        List<OrderLine> items
) {
    public record OrderLine(
            String productId,
            int quantity,
            BigDecimal unitPrice
    ) {
    }
}
