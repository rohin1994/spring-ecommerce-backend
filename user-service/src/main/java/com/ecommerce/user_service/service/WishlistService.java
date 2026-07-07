package com.ecommerce.user_service.service;

import com.ecommerce.user_service.model.dto.response.WishlistItemResponse;

public interface WishlistService {

    WishlistItemResponse getItem(String userId, String productId);

    WishlistItemResponse addItem(String userId, String productId);

    void removeItem(String userId, String productId);
}
