package com.ecommerce.product_service.service.impl;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.product_service.model.dto.request.ProductCreateRequest;
import com.ecommerce.product_service.model.dto.request.ProductUpdateRequest;
import com.ecommerce.product_service.model.dto.response.ProductDetailDTO;
import com.ecommerce.product_service.model.dto.response.ProductSummaryDTO;
import com.ecommerce.product_service.model.entity.Category;
import com.ecommerce.product_service.model.entity.Product;
import com.ecommerce.product_service.model.enums.ProductStatus;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.CategoryService;
import com.ecommerce.product_service.service.DiscountService;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final DiscountService discountService;

    @Override
    public ProductDetailDTO createProduct(ProductCreateRequest request) {
        Category category = categoryService.getByCode(request.getCategoryCode());

        if (productRepository.existsByNameIgnoreCaseAndCategory_Code(request.getName(), request.getCategoryCode())) {
            throw new BusinessException("Product with this name already exists in category");
        }

        Product product = new Product(
                request.getName(),
                request.getBasePrice(),
                request.getCurrency(),
                category
        );

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getImageUrls() != null) {
            product.setImageUrls(new ArrayList<>(request.getImageUrls()));
        }

        return mapToDetailDTO(productRepository.save(product));
    }

    @Override
    public ProductDetailDTO updateProduct(String id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found: " + id, HttpStatus.NOT_FOUND));

        Category category = categoryService.getByCode(request.getCategoryCode());

        if (!product.getName().equalsIgnoreCase(request.getName())
                && productRepository.existsByNameIgnoreCaseAndCategory_Code(request.getName(), request.getCategoryCode())) {
            throw new BusinessException("Product with this name already exists in category");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        product.setCurrency(request.getCurrency());
        product.setCategory(category);
        if (request.getImageUrls() != null) {
            product.setImageUrls(new ArrayList<>(request.getImageUrls()));
        }
        if (request.getStatus() != null) {
            product.setStatus(ProductStatus.valueOf(request.getStatus()));
        }

        return mapToDetailDTO(productRepository.save(product));
    }

    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException("Product not found: " + id, HttpStatus.NOT_FOUND);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found: " + id, HttpStatus.NOT_FOUND));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BusinessException("Product is not active: " + id);
        }

        return mapToDetailDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getAdminProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found: " + id, HttpStatus.NOT_FOUND));
        return mapToDetailDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryDTO> getAllActiveProducts(Pageable pageable) {
        return productRepository.findByStatus(ProductStatus.ACTIVE.name(), pageable)
                .map(this::mapToSummaryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToSummaryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryDTO> getProductsByCategory(String categoryCode) {
        categoryService.getByCode(categoryCode);

        return productRepository.findByCategory_CodeAndStatus(categoryCode, ProductStatus.ACTIVE.name()).stream()
                .map(this::mapToSummaryDTO)
                .toList();
    }

    private ProductDetailDTO mapToDetailDTO(Product product) {
        DiscountService.AppliedDiscount appliedDiscount = discountService.findBestDiscountForProduct(product).orElse(null);

        return new ProductDetailDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory().getCode(),
                product.getCategory().getName(),
                product.getBasePrice(),
                appliedDiscount != null ? appliedDiscount.discountedPrice() : null,
                appliedDiscount != null ? appliedDiscount.discountLabel() : null,
                product.getCurrency(),
                product.getStatus().name(),
                product.getImageUrls(),
                null
        );
    }

    private ProductSummaryDTO mapToSummaryDTO(Product product) {
        DiscountService.AppliedDiscount appliedDiscount = discountService.findBestDiscountForProduct(product).orElse(null);

        return new ProductSummaryDTO(
                product.getId(),
                product.getName(),
                product.getCategory().getCode(),
                product.getBasePrice(),
                appliedDiscount != null ? appliedDiscount.discountedPrice() : null,
                appliedDiscount != null ? appliedDiscount.discountLabel() : null,
                product.getCurrency()
        );
    }
}
