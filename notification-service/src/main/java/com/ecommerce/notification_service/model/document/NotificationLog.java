package com.ecommerce.notification_service.model.document;

import com.ecommerce.notification_service.model.enums.NotificationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notification_logs")
public class NotificationLog {

    @Id
    private String id;

    private String orderId;

    private NotificationType type;

    private String message;

    private Instant sentAt;

    protected NotificationLog() {
    }

    public NotificationLog(String orderId, NotificationType type, String message) {
        this.orderId = orderId;
        this.type = type;
        this.message = message;
        this.sentAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}
