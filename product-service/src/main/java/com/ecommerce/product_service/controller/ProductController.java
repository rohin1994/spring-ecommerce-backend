package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.model.dto.response.ProductDetailDTO;
import com.ecommerce.product_service.model.dto.response.ProductSummaryDTO;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/category/{categoryCode}")
    public ResponseEntity<List<ProductSummaryDTO>> getProductsByCategory(@PathVariable String categoryCode) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryCode));
    }

    @GetMapping
    public ResponseEntity<Page<ProductSummaryDTO>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllActiveProducts(pageable));
    }
}
