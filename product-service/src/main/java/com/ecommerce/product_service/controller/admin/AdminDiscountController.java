package com.ecommerce.product_service.controller.admin;

import com.ecommerce.product_service.model.dto.request.DiscountCreateRequest;
import com.ecommerce.product_service.model.dto.request.DiscountUpdateRequest;
import com.ecommerce.product_service.model.dto.response.DiscountResponse;
import com.ecommerce.product_service.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/discounts")
@RequiredArgsConstructor
public class AdminDiscountController {

    private final DiscountService discountService;

    @GetMapping
    @PreAuthorize("hasAuthority('discount:read')")
    public ResponseEntity<List<DiscountResponse>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('discount:read')")
    public ResponseEntity<DiscountResponse> getDiscountById(@PathVariable String id) {
        return ResponseEntity.ok(discountService.getDiscountById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('discount:write')")
    public ResponseEntity<DiscountResponse> createDiscount(@Valid @RequestBody DiscountCreateRequest request) {
        DiscountResponse discount = discountService.createDiscount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(discount);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('discount:write')")
    public ResponseEntity<DiscountResponse> updateDiscount(
            @PathVariable String id,
            @Valid @RequestBody DiscountUpdateRequest request) {
        return ResponseEntity.ok(discountService.updateDiscount(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('discount:delete')")
    public ResponseEntity<Void> deleteDiscount(@PathVariable String id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
}
