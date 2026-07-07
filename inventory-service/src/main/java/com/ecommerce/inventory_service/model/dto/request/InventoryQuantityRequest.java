package com.ecommerce.inventory_service.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryQuantityRequest {

    @NotNull
    @Min(1)
    private Integer quantity;
}
