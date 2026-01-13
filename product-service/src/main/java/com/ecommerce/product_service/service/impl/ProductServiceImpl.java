package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.exception.BusinessException;
import com.ecommerce.product_service.model.dto.request.ProductCreateRequest;
import com.ecommerce.product_service.model.dto.response.ProductDetailDTO;
import com.ecommerce.product_service.model.dto.response.ProductSummaryDTO;
import com.ecommerce.product_service.model.entity.Category;
import com.ecommerce.product_service.model.entity.Product;
import com.ecommerce.product_service.model.enums.ProductStatus;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.CategoryService;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Override
    public ProductDetailDTO createProduct(ProductCreateRequest request) {
        // Get category by code
        Category category = categoryService.getByCode(request.getCategoryCode());

        // Check for product name uniqueness within category
        if (productRepository.existsByNameIgnoreCaseAndCategory_Code(request.getName(), request.getCategoryCode())) {
            throw new BusinessException("Product with this name already exists in category");
        }

        // Create product using constructor with default status ACTIVE
        Product product = new Product(
                request.getName(),
                request.getBasePrice(),
                request.getCurrency(),
                category
        );

        // Set optional description
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription()); // <-- FIXED
        }

        Product saved = productRepository.save(product);

        return mapToDetailDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found: " + id));

        // Ensure product is ACTIVE
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BusinessException("Product is not active: " + id);
        }

        return mapToDetailDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryDTO> getProductsByCategory(String categoryCode) {
        Category category = categoryService.getByCode(categoryCode);

        List<Product> products = productRepository.findByCategory_CodeAndStatus(categoryCode, ProductStatus.ACTIVE.toString());

        return products.stream()
                .map(this::mapToSummaryDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryDTO> getAllActiveProducts() {
        List<Product> products = productRepository.findByStatus(ProductStatus.ACTIVE.toString());

        return products.stream()
                .map(this::mapToSummaryDTO)
                .toList();
    }

    // -------------------
    // Private Mapper Methods
    // -------------------

    private ProductDetailDTO mapToDetailDTO(Product product) {
        return new ProductDetailDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory().getCode(),
                product.getCategory().getName(),
                product.getBasePrice(),
                product.getCurrency(),
                product.getStatus().name(),
                null // attributes placeholder
        );
    }

    private ProductSummaryDTO mapToSummaryDTO(Product product) {
        return new ProductSummaryDTO(
                product.getId(),
                product.getName(),
                product.getCategory().getCode(),
                product.getBasePrice(),
                product.getCurrency()
        );
    }
}