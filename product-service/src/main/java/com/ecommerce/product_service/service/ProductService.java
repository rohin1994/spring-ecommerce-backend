package com.ecommerce.product_service.service;

import com.ecommerce.product_service.model.dto.request.ProductCreateRequest;
import com.ecommerce.product_service.model.dto.request.ProductUpdateRequest;
import com.ecommerce.product_service.model.dto.response.ProductDetailDTO;
import com.ecommerce.product_service.model.dto.response.ProductSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductDetailDTO createProduct(ProductCreateRequest request);

    ProductDetailDTO updateProduct(String id, ProductUpdateRequest request);

    void deleteProduct(String id);

    ProductDetailDTO getProductById(String id);

    ProductDetailDTO getAdminProductById(String id);

    Page<ProductSummaryDTO> getAllActiveProducts(Pageable pageable);

    Page<ProductSummaryDTO> getAllProducts(Pageable pageable);

    List<ProductSummaryDTO> getProductsByCategory(String categoryCode);
}
