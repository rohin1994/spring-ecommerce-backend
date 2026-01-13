package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.model.dto.request.ProductCreateRequest;
import com.ecommerce.product_service.model.dto.response.ProductDetailDTO;
import com.ecommerce.product_service.model.dto.response.ProductSummaryDTO;
import com.ecommerce.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Create a product
    @PostMapping
    public ResponseEntity<ProductDetailDTO> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        ProductDetailDTO product = productService.createProduct(request);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductById(@PathVariable String id) {
        ProductDetailDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // Get products by category
    @GetMapping("/category/{categoryCode}")
    public ResponseEntity<List<ProductSummaryDTO>> getProductsByCategory(@PathVariable String categoryCode) {
        List<ProductSummaryDTO> products = productService.getProductsByCategory(categoryCode);
        return ResponseEntity.ok(products);
    }

    // Get all active products (summary)
    @GetMapping
    public ResponseEntity<List<ProductSummaryDTO>> getAllProducts() {
        List<ProductSummaryDTO> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }
}
