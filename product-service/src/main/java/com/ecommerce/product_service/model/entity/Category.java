package com.ecommerce.product_service.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "categories")
public class Category {

    @Id
    private String id;   // MongoDB generates ObjectId

    private String code;   // BUSINESS IDENTIFIER
    private String name;   // DISPLAY NAME
    private boolean active = true;

    public Category(String code, String name) {
        this.code = code;
        this.name = name;
        this.active = true; // default
    }
}