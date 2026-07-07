package com.ecommerce.product_service.controller.admin;

import com.ecommerce.product_service.model.dto.request.ProductCreateRequest;
import com.ecommerce.product_service.model.dto.request.ProductUpdateRequest;
import com.ecommerce.product_service.model.dto.response.ProductDetailDTO;
import com.ecommerce.product_service.model.dto.response.ProductSummaryDTO;
import com.ecommerce.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAuthority('product:read')")
    public ResponseEntity<Page<ProductSummaryDTO>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product:read')")
    public ResponseEntity<ProductDetailDTO> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getAdminProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:write')")
    public ResponseEntity<ProductDetailDTO> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductDetailDTO product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:write')")
    public ResponseEntity<ProductDetailDTO> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
