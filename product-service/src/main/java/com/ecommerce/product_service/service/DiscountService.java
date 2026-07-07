package com.ecommerce.product_service.service;

import com.ecommerce.product_service.model.dto.request.DiscountCreateRequest;
import com.ecommerce.product_service.model.dto.request.DiscountUpdateRequest;
import com.ecommerce.product_service.model.dto.response.DiscountResponse;
import com.ecommerce.product_service.model.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DiscountService {

    DiscountResponse createDiscount(DiscountCreateRequest request);

    DiscountResponse updateDiscount(String id, DiscountUpdateRequest request);

    DiscountResponse getDiscountById(String id);

    List<DiscountResponse> getAllDiscounts();

    void deleteDiscount(String id);

    Optional<AppliedDiscount> findBestDiscountForProduct(Product product);

    record AppliedDiscount(BigDecimal discountedPrice, String discountLabel) {
    }
}
