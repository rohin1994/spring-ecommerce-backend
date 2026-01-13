package com.ecommerce.product_service.model.dto.request;

import com.ecommerce.product_service.model.dto.common.AttributeDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String categoryCode;   // Reference to Category

    @NotNull
    private BigDecimal basePrice;

    @NotBlank
    private String currency;

    private List<AttributeDTO> attributes;
}