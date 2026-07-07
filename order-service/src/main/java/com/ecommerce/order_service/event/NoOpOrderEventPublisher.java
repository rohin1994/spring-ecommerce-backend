package com.ecommerce.order_service.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "order.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpOrderEventPublisher implements OrderEventPublisher {

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        // Kafka disabled — no-op stub for local development
    }
}
