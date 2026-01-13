package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.model.dto.request.CategoryCreateRequest;
import com.ecommerce.product_service.model.entity.Category;
import com.ecommerce.product_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Create a new category
    @PostMapping
    public ResponseEntity<Category> createCategory(
            @Valid @RequestBody CategoryCreateRequest request) {
        Category category = categoryService.createCategory(request);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    // Get all active categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }

    // Get a category by code
    @GetMapping("/{code}")
    public ResponseEntity<Category> getCategoryByCode(@PathVariable String code) {
        Category category = categoryService.getByCode(code);
        return ResponseEntity.ok(category);
    }
}