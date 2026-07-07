package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    boolean existsByNameIgnoreCaseAndCategory_Id(String name, String categoryId);

    boolean existsByNameIgnoreCaseAndCategory_Code(String name, String categoryCode);

    List<Product> findByCategory_CodeAndStatus(String categoryCode, String status);

    Page<Product> findByStatus(String status, Pageable pageable);

    List<Product> findByStatus(String status);
}
