package com.ecommerce.product_service.model.dto.request;

import com.ecommerce.product_service.model.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor
public class DiscountUpdateRequest {

    @NotBlank
    private String code;

    @NotNull
    private DiscountType type;

    @NotNull
    private BigDecimal value;

    private List<String> productIds;
    private List<String> categoryCodes;
    private Instant startsAt;
    private Instant endsAt;
    private boolean active;
}
