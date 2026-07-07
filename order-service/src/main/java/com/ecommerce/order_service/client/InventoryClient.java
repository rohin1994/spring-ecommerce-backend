package com.ecommerce.order_service.client;

import com.ecommerce.order_service.client.fallback.InventoryClientFallback;
import com.ecommerce.order_service.model.dto.request.ReserveInventoryRequest;
import com.ecommerce.order_service.model.dto.response.ReserveInventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", path = "/internal/v1/inventory", fallback = InventoryClientFallback.class)
public interface InventoryClient {

    @PostMapping("/{productId}/reserve")
    ReserveInventoryResponse reserve(
            @PathVariable("productId") String productId,
            @RequestBody ReserveInventoryRequest request);
}
