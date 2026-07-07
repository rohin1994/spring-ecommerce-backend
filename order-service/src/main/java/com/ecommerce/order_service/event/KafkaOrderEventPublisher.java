package com.ecommerce.order_service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "order.kafka.enabled", havingValue = "true")
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Value("${order.kafka.topic}")
    private String topic;

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(topic, event.getOrderId(), event);
    }
}
