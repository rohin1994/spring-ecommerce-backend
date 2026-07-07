package com.ecommerce.notification_service.model.event;

public record PaymentFailedEvent(
        String orderId,
        String reason
) {
}
