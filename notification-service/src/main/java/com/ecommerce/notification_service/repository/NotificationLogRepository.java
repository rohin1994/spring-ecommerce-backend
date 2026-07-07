package com.ecommerce.notification_service.repository;

import com.ecommerce.notification_service.model.document.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {

    List<NotificationLog> findByOrderId(String orderId);
}
