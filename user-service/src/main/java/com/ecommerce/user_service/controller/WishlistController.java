package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.model.dto.response.WishlistItemResponse;
import com.ecommerce.user_service.security.CurrentUser;
import com.ecommerce.user_service.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping("/{productId}")
    public ResponseEntity<WishlistItemResponse> getWishlistItem(@PathVariable String productId) {
        return ResponseEntity.ok(wishlistService.getItem(CurrentUser.requireUserId(), productId));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<WishlistItemResponse> addWishlistItem(@PathVariable String productId) {
        WishlistItemResponse response = wishlistService.addItem(CurrentUser.requireUserId(), productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeWishlistItem(@PathVariable String productId) {
        wishlistService.removeItem(CurrentUser.requireUserId(), productId);
        return ResponseEntity.noContent().build();
    }
}
