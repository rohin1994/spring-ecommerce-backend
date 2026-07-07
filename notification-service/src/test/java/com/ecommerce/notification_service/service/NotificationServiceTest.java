package com.ecommerce.notification_service.service;

import com.ecommerce.notification_service.model.document.NotificationLog;
import com.ecommerce.notification_service.model.enums.NotificationType;
import com.ecommerce.notification_service.repository.NotificationLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendOrderConfirmed_persistsLog() {
        when(notificationLogRepository.save(org.mockito.ArgumentMatchers.any(NotificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationLog result = notificationService.sendOrderConfirmed("order-10", "Order confirmed");

        assertThat(result.getOrderId()).isEqualTo("order-10");
        assertThat(result.getType()).isEqualTo(NotificationType.ORDER_CONFIRMED);
        assertThat(result.getMessage()).isEqualTo("Order confirmed");
        assertThat(result.getSentAt()).isNotNull();

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo(NotificationType.ORDER_CONFIRMED);
    }
}
