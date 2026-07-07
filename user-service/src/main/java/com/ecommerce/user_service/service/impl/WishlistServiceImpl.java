package com.ecommerce.user_service.service.impl;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.user_service.model.dto.response.WishlistItemResponse;
import com.ecommerce.user_service.model.entity.WishlistItem;
import com.ecommerce.user_service.repository.WishlistItemRepository;
import com.ecommerce.user_service.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepository;

    @Override
    @Transactional(readOnly = true)
    public WishlistItemResponse getItem(String userId, String productId) {
        WishlistItem item = wishlistItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new BusinessException("Wishlist item not found", HttpStatus.NOT_FOUND));
        return toResponse(item);
    }

    @Override
    @Transactional
    public WishlistItemResponse addItem(String userId, String productId) {
        WishlistItem item = wishlistItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(() -> WishlistItem.builder()
                        .userId(userId)
                        .productId(productId)
                        .addedAt(Instant.now())
                        .build());

        if (item.getId() == null) {
            item = wishlistItemRepository.save(item);
        }
        return toResponse(item);
    }

    @Override
    @Transactional
    public void removeItem(String userId, String productId) {
        if (!wishlistItemRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new BusinessException("Wishlist item not found", HttpStatus.NOT_FOUND);
        }
        wishlistItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    private WishlistItemResponse toResponse(WishlistItem item) {
        return WishlistItemResponse.builder()
                .productId(item.getProductId())
                .addedAt(item.getAddedAt())
                .build();
    }
}
