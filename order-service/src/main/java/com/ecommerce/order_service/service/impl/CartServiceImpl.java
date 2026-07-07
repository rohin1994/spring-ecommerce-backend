package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.model.dto.request.AddCartItemRequest;
import com.ecommerce.order_service.model.dto.response.CartItemResponse;
import com.ecommerce.order_service.model.dto.response.CartResponse;
import com.ecommerce.order_service.model.entity.CartItem;
import com.ecommerce.order_service.repository.CartItemRepository;
import com.ecommerce.order_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        List<CartItemResponse> items = cartItemRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
        return CartResponse.builder().items(items).build();
    }

    @Override
    @Transactional
    public CartResponse addItem(String userId, AddCartItemRequest request) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId())
                .orElseGet(() -> CartItem.builder()
                        .userId(userId)
                        .productId(request.getProductId())
                        .quantity(0)
                        .build());
        item.setQuantity(item.getQuantity() + request.getQuantity());
        cartItemRepository.save(item);
        return getCart(userId);
    }

    @Override
    @Transactional
    public void removeItem(String userId, String productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    private CartItemResponse toResponse(CartItem item) {
        return CartItemResponse.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .build();
    }
}
