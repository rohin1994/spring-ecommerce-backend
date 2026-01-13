package com.ecommerce.product_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductSummaryDTO {
    private String id;
    private String name;
    private String categoryCode;
    private BigDecimal basePrice;
    private String currency;
}