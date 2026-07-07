package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.model.dto.request.InventoryQuantityRequest;
import com.ecommerce.inventory_service.model.dto.response.InventoryResponse;

public interface InventoryService {

    InventoryResponse getInventory(String productId);

    InventoryResponse reserve(String productId, InventoryQuantityRequest request);

    InventoryResponse release(String productId, InventoryQuantityRequest request);
}
