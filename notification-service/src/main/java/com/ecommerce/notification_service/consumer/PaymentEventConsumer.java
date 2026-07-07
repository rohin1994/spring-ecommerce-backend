package com.ecommerce.notification_service.consumer;

import com.ecommerce.notification_service.model.event.PaymentCompletedEvent;
import com.ecommerce.notification_service.model.event.PaymentFailedEvent;
import com.ecommerce.notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final NotificationService notificationService;

    public PaymentEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.payment-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderConfirmed(PaymentCompletedEvent event) {
        log.info("Treating payment-completed as OrderConfirmed orderId={}", event.orderId());
        String message = "Your order #" + event.orderId() + " is confirmed. Payment of "
                + event.amount() + " was successful.";
        notificationService.sendOrderConfirmed(event.orderId(), message);
    }

    @KafkaListener(topics = "${app.kafka.topics.payment-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("Received PaymentFailed orderId={}", event.orderId());
        String message = "Payment for order #" + event.orderId() + " could not be processed. "
                + event.reason();
        notificationService.sendPaymentFailed(event.orderId(), message);
    }
}
