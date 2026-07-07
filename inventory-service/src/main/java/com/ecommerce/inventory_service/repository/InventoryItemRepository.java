package com.ecommerce.inventory_service.repository;

import com.ecommerce.inventory_service.model.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, String> {
}
