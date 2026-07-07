package com.ecommerce.payment_service.service;

import com.ecommerce.payment_service.model.entity.Payment;
import com.ecommerce.payment_service.model.enums.PaymentStatus;
import com.ecommerce.payment_service.model.event.PaymentCompletedEvent;
import com.ecommerce.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    @SuppressWarnings("unchecked")
    private ObjectProvider<KafkaTemplate<String, Object>> kafkaTemplateProvider;

    @Test
    void processOrderPayment_alwaysSucceedsAndPublishesEvent() {
        when(kafkaTemplateProvider.getIfAvailable()).thenReturn(kafkaTemplate);
        PaymentService service = new PaymentService(paymentRepository, kafkaTemplateProvider, "payment-completed");
        String orderId = "order-42";

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = service.processOrderPayment(orderId, new BigDecimal("99.99"));

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);

        ArgumentCaptor<PaymentCompletedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
        verify(kafkaTemplate).send(eq("payment-completed"), eq(orderId), eventCaptor.capture());
        assertThat(eventCaptor.getValue().orderId()).isEqualTo(orderId);
        assertThat(eventCaptor.getValue().amount()).isEqualByComparingTo("99.99");
    }
}
