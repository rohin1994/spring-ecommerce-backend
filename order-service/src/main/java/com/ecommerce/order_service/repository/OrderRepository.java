package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByIdAndUserId(String id, String userId);
}
