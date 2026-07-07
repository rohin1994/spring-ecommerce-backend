package com.ecommerce.inventory_service.model.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InventoryResponse {

    String productId;
    int quantity;
    int reserved;
    int available;
}
