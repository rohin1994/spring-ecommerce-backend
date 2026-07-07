package com.ecommerce.notification_service.model.event;

import java.math.BigDecimal;

public record PaymentCompletedEvent(
        String orderId,
        Long paymentId,
        BigDecimal amount
) {
}
