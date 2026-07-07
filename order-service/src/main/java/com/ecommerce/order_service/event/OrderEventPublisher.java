package com.ecommerce.order_service.event;

public interface OrderEventPublisher {

    void publishOrderCreated(OrderCreatedEvent event);
}
