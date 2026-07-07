package com.ecommerce.product_service.model.entity;

import com.ecommerce.product_service.model.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "discounts")
public class Discount {

    @Id
    private String id;

    private String code;
    private DiscountType type;
    private BigDecimal value;
    private List<String> productIds = new ArrayList<>();
    private List<String> categoryCodes = new ArrayList<>();
    private Instant startsAt;
    private Instant endsAt;
    private boolean active;
}
