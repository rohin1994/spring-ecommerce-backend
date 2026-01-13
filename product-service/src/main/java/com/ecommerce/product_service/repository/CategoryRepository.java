package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.model.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByNameIgnoreCase(String name);
}