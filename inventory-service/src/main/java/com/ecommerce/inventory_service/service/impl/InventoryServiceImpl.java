package com.ecommerce.inventory_service.service.impl;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.inventory_service.model.dto.request.InventoryQuantityRequest;
import com.ecommerce.inventory_service.model.dto.response.InventoryResponse;
import com.ecommerce.inventory_service.model.entity.InventoryItem;
import com.ecommerce.inventory_service.repository.InventoryItemRepository;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(String productId) {
        InventoryItem item = inventoryItemRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Inventory not found", HttpStatus.NOT_FOUND));
        return toResponse(item);
    }

    @Override
    @Transactional
    public InventoryResponse reserve(String productId, InventoryQuantityRequest request) {
        InventoryItem item = inventoryItemRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Inventory not found", HttpStatus.NOT_FOUND));

        int requested = request.getQuantity();
        if (item.available() < requested) {
            throw new BusinessException(
                    "Only " + item.available() + " items available.",
                    HttpStatus.CONFLICT
            );
        }

        item.setReserved(item.getReserved() + requested);
        return toResponse(inventoryItemRepository.save(item));
    }

    @Override
    @Transactional
    public InventoryResponse release(String productId, InventoryQuantityRequest request) {
        InventoryItem item = inventoryItemRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Inventory not found", HttpStatus.NOT_FOUND));

        int releaseQty = Math.min(request.getQuantity(), item.getReserved());
        item.setReserved(item.getReserved() - releaseQty);
        return toResponse(inventoryItemRepository.save(item));
    }

    private InventoryResponse toResponse(InventoryItem item) {
        return InventoryResponse.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .reserved(item.getReserved())
                .available(item.available())
                .build();
    }
}
