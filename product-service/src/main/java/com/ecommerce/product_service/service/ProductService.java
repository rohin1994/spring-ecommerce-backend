package com.ecommerce.product_service.service;

import com.ecommerce.product_service.model.dto.request.ProductCreateRequest;
import com.ecommerce.product_service.model.dto.response.ProductDetailDTO;
import com.ecommerce.product_service.model.dto.response.ProductSummaryDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductDetailDTO createProduct(ProductCreateRequest request);

    ProductDetailDTO getProductById(String id);

    List<ProductSummaryDTO> getProductsByCategory(String categoryCode);

    List<ProductSummaryDTO> getAllActiveProducts();
}