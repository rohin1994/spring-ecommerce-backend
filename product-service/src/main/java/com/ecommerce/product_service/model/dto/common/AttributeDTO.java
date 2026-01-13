package com.ecommerce.product_service.model.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDTO {
    @NotNull
    private String name;

    @NotNull
    private String value;
}
