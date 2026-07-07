package com.ecommerce.order_service.service;

import com.ecommerce.order_service.model.dto.request.AddCartItemRequest;
import com.ecommerce.order_service.model.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(String userId);

    CartResponse addItem(String userId, AddCartItemRequest request);

    void removeItem(String userId, String productId);
}
