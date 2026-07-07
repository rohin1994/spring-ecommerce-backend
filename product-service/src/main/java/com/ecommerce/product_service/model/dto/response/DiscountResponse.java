package com.ecommerce.product_service.model.dto.response;

import com.ecommerce.product_service.model.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class DiscountResponse {
    private String id;
    private String code;
    private DiscountType type;
    private BigDecimal value;
    private List<String> productIds;
    private List<String> categoryCodes;
    private Instant startsAt;
    private Instant endsAt;
    private boolean active;
}
