package com.ecommerce.user_service.repository;

import com.ecommerce.user_service.model.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    Optional<WishlistItem> findByUserIdAndProductId(String userId, String productId);

    void deleteByUserIdAndProductId(String userId, String productId);
}
