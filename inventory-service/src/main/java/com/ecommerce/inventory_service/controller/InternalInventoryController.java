package com.ecommerce.inventory_service.controller;

import com.ecommerce.inventory_service.model.dto.request.InventoryQuantityRequest;
import com.ecommerce.inventory_service.model.dto.response.InventoryResponse;
import com.ecommerce.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/inventory")
@RequiredArgsConstructor
public class InternalInventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable String productId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<InventoryResponse> reserve(@PathVariable String productId,
                                                     @Valid @RequestBody InventoryQuantityRequest request) {
        return ResponseEntity.ok(inventoryService.reserve(productId, request));
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<InventoryResponse> release(@PathVariable String productId,
                                                     @Valid @RequestBody InventoryQuantityRequest request) {
        return ResponseEntity.ok(inventoryService.release(productId, request));
    }
}
