package com.ecommerce.product_service.model.dto.response;

import com.ecommerce.product_service.model.dto.common.AttributeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProductDetailDTO {
    private String id;
    private String name;
    private String description;
    private String categoryCode;
    private String categoryName;
    private BigDecimal basePrice;
    private BigDecimal discountedPrice;
    private String discountLabel;
    private String currency;
    private String status;
    private List<String> imageUrls;
    private List<AttributeDTO> attributes;
}
