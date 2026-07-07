package com.ecommerce.payment_service.service;

import com.ecommerce.payment_service.model.entity.Payment;
import com.ecommerce.payment_service.model.enums.PaymentStatus;
import com.ecommerce.payment_service.model.event.PaymentCompletedEvent;
import com.ecommerce.payment_service.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final ObjectProvider<KafkaTemplate<String, Object>> kafkaTemplateProvider;
    private final String paymentCompletedTopic;

    public PaymentService(
            PaymentRepository paymentRepository,
            ObjectProvider<KafkaTemplate<String, Object>> kafkaTemplateProvider,
            @Value("${app.kafka.topics.payment-completed}") String paymentCompletedTopic) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplateProvider = kafkaTemplateProvider;
        this.paymentCompletedTopic = paymentCompletedTopic;
    }

    @Transactional
    public Payment processOrderPayment(String orderId, BigDecimal amount) {
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            log.warn("Payment already exists for orderId={}", orderId);
            return paymentRepository.findByOrderId(orderId).orElseThrow();
        }

        Payment payment = new Payment(orderId, amount, PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);
        log.info("Created payment id={} orderId={} amount={}", payment.getId(), orderId, amount);

        if (simulatePaymentGateway(payment)) {
            payment.markCompleted();
            paymentRepository.save(payment);
            publishPaymentCompleted(payment);
            log.info("Payment completed id={} orderId={}", payment.getId(), orderId);
        } else {
            payment.markFailed();
            paymentRepository.save(payment);
            log.warn("Payment failed id={} orderId={}", payment.getId(), orderId);
        }

        return payment;
    }

    /**
     * Draft mock gateway — always succeeds.
     */
    boolean simulatePaymentGateway(Payment payment) {
        log.debug("Simulating payment gateway for orderId={}", payment.getOrderId());
        return true;
    }

    private void publishPaymentCompleted(Payment payment) {
        KafkaTemplate<String, Object> kafkaTemplate = kafkaTemplateProvider.getIfAvailable();
        if (kafkaTemplate == null) {
            log.debug("Kafka disabled; skipping PaymentCompleted publish for orderId={}", payment.getOrderId());
            return;
        }
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                payment.getOrderId(),
                payment.getId(),
                payment.getAmount()
        );
        kafkaTemplate.send(paymentCompletedTopic, String.valueOf(payment.getOrderId()), event);
    }
}
