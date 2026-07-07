package com.ecommerce.product_service.service;

import com.ecommerce.product_service.model.dto.request.CategoryCreateRequest;
import com.ecommerce.product_service.model.entity.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(CategoryCreateRequest request);

    List<Category> getAllActiveCategories();

    List<Category> getAllCategories();

    Category getByCode(String code);
}
