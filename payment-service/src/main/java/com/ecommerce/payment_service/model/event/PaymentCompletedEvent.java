package com.ecommerce.payment_service.model.event;

import java.math.BigDecimal;

public record PaymentCompletedEvent(
        String orderId,
        Long paymentId,
        BigDecimal amount
) {
}
