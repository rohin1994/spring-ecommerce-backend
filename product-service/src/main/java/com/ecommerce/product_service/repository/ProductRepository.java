package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.model.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    // Check if a product with this name exists in a given category (by ID)
    boolean existsByNameIgnoreCaseAndCategory_Id(String name, String categoryId);

    // Optional: check by category code
    boolean existsByNameIgnoreCaseAndCategory_Code(String name, String categoryCode);

    // Find products by category code
    List<Product> findByCategory_CodeAndStatus(String categoryCode, String status);

    // Find all active products
    List<Product> findByStatus(String status);
}