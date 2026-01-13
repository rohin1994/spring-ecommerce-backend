package com.ecommerce.product_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryCreateRequest {

    @NotBlank
    private String code;   // ELECTRONICS, FASHION

    @NotBlank
    private String name;   // Electronics & Gadgets, Fashion
}