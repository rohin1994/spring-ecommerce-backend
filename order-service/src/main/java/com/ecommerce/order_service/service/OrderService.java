package com.ecommerce.order_service.service;

import com.ecommerce.order_service.model.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse checkout(String userId);

    OrderResponse getOrder(String userId, String orderId);
}
