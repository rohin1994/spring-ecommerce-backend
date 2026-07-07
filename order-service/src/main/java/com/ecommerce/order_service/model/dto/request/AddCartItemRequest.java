package com.ecommerce.order_service.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCartItemRequest {

    @NotBlank
    private String productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
