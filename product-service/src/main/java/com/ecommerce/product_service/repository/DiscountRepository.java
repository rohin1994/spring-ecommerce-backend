package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.model.entity.Discount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DiscountRepository extends MongoRepository<Discount, String> {

    Optional<Discount> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);
}
