package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.config.SecurityConfig;
import com.ecommerce.product_service.model.dto.response.ProductDetailDTO;
import com.ecommerce.product_service.model.dto.response.ProductSummaryDTO;
import com.ecommerce.product_service.security.AdminPermissionFilter;
import com.ecommerce.product_service.service.ProductService;
import com.ecommerce.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@Import({SecurityConfig.class, AdminPermissionFilter.class, GlobalExceptionHandler.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void getAllProducts_returnsPaginatedSummaries() throws Exception {
        ProductSummaryDTO summary = new ProductSummaryDTO(
                "prod-1",
                "Phone",
                "electronics",
                new BigDecimal("999.99"),
                new BigDecimal("899.99"),
                "10% OFF",
                "USD"
        );
        when(productService.getAllActiveProducts(any())).thenReturn(new PageImpl<>(
                List.of(summary),
                PageRequest.of(0, 20),
                1
        ));

        mockMvc.perform(get("/api/v1/products").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("prod-1"))
                .andExpect(jsonPath("$.content[0].discountedPrice").value(899.99))
                .andExpect(jsonPath("$.content[0].discountLabel").value("10% OFF"));
    }

    @Test
    void getProductById_returnsDetail() throws Exception {
        ProductDetailDTO detail = new ProductDetailDTO(
                "prod-1",
                "Phone",
                "Latest model",
                "electronics",
                "Electronics",
                new BigDecimal("999.99"),
                new BigDecimal("899.99"),
                "10% OFF",
                "USD",
                "ACTIVE",
                List.of("https://cdn.example.com/phone.jpg"),
                null
        );
        when(productService.getProductById("prod-1")).thenReturn(detail);

        mockMvc.perform(get("/api/v1/products/prod-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("prod-1"))
                .andExpect(jsonPath("$.discountedPrice").value(899.99))
                .andExpect(jsonPath("$.imageUrls[0]").value("https://cdn.example.com/phone.jpg"));
    }

    @Test
    void getProductsByCategory_returnsSummaries() throws Exception {
        ProductSummaryDTO summary = new ProductSummaryDTO(
                "prod-1",
                "Phone",
                "electronics",
                new BigDecimal("999.99"),
                null,
                null,
                "USD"
        );
        when(productService.getProductsByCategory("electronics")).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/products/category/electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryCode").value("electronics"));
    }
}
