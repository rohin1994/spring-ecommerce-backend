package com.ecommerce.inventory_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {

    @Id
    @Column(name = "product_id", length = 36)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int reserved;

    public int available() {
        return quantity - reserved;
    }
}
