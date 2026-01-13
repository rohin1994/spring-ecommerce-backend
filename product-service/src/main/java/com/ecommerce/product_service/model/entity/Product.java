package com.ecommerce.product_service.model.entity;

import com.ecommerce.product_service.model.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;
    private String description;
    private BigDecimal basePrice;
    private String currency;

    private ProductStatus status;

    @DBRef
    private Category category;  // reference category by ID

    public Product(String name, BigDecimal basePrice, String currency, Category category) {
        this.name = name;
        this.basePrice = basePrice;
        this.currency = currency;
        this.category = category;
        this.status = ProductStatus.ACTIVE; // default
    }
}