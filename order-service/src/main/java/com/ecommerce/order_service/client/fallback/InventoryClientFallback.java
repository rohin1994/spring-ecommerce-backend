package com.ecommerce.order_service.client.fallback;

import com.ecommerce.order_service.client.InventoryClient;
import com.ecommerce.order_service.model.dto.request.ReserveInventoryRequest;
import com.ecommerce.order_service.model.dto.response.ReserveInventoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InventoryClientFallback implements InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClientFallback.class);

    @Override
    public ReserveInventoryResponse reserve(String productId, ReserveInventoryRequest request) {
        log.warn("Inventory service unavailable, returning fallback for productId={}", productId);
        return ReserveInventoryResponse.builder()
                .productId(productId)
                .reserved(0)
                .available(0)
                .build();
    }
}
