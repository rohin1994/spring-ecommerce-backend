package com.ecommerce.notification_service.service;

import com.ecommerce.notification_service.model.document.NotificationLog;
import com.ecommerce.notification_service.model.enums.NotificationType;
import com.ecommerce.notification_service.repository.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationLogRepository notificationLogRepository;

    public NotificationService(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    public NotificationLog sendOrderConfirmed(String orderId, String message) {
        return send(orderId, NotificationType.ORDER_CONFIRMED, message);
    }

    public NotificationLog sendPaymentFailed(String orderId, String message) {
        return send(orderId, NotificationType.PAYMENT_FAILED, message);
    }

    private NotificationLog send(String orderId, NotificationType type, String message) {
        log.info("[EMAIL STUB] type={} orderId={} message={}", type, orderId, message);
        NotificationLog notificationLog = new NotificationLog(orderId, type, message);
        return notificationLogRepository.save(notificationLog);
    }
}
