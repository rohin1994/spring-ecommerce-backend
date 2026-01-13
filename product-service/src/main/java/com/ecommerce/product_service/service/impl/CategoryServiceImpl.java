package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.exception.BusinessException;
import com.ecommerce.product_service.model.dto.request.CategoryCreateRequest;
import com.ecommerce.product_service.model.entity.Category;
import com.ecommerce.product_service.repository.CategoryRepository;
import com.ecommerce.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryCreateRequest request) {
        if (categoryRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Category code already exists");
        }
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException("Category name already exists");
        }

        Category category = new Category(request.getCode(), request.getName());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAll()
                .stream()
                .filter(Category::isActive)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getByCode(String code) {
        return categoryRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Category not found: " + code));
    }
}