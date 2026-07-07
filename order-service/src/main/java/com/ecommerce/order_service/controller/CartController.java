package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.model.dto.request.AddCartItemRequest;
import com.ecommerce.order_service.model.dto.response.CartResponse;
import com.ecommerce.order_service.security.CurrentUser;
import com.ecommerce.order_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart(CurrentUser.requireUserId()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody AddCartItemRequest request) {
        CartResponse cart = cartService.addItem(CurrentUser.requireUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable String productId) {
        cartService.removeItem(CurrentUser.requireUserId(), productId);
        return ResponseEntity.noContent().build();
    }
}
