package com.ecommerce.product_service.controller.admin;

import com.ecommerce.product_service.config.SecurityConfig;
import com.ecommerce.product_service.config.SecurityExceptionHandler;
import com.ecommerce.product_service.model.dto.request.ProductCreateRequest;
import com.ecommerce.product_service.model.dto.response.ProductDetailDTO;
import com.ecommerce.product_service.security.AdminPermissionFilter;
import com.ecommerce.product_service.service.ProductService;
import com.ecommerce.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminProductController.class)
@Import({SecurityConfig.class, AdminPermissionFilter.class, GlobalExceptionHandler.class, SecurityExceptionHandler.class})
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    @WithMockUser(authorities = "product:write")
    void createProduct_withWritePermission_returnsCreated() throws Exception {
        ProductDetailDTO created = new ProductDetailDTO(
                "prod-1",
                "Phone",
                "Latest model",
                "electronics",
                "Electronics",
                new BigDecimal("999.99"),
                null,
                null,
                "USD",
                "ACTIVE",
                List.of(),
                null
        );
        when(productService.createProduct(any(ProductCreateRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Phone",
                                  "description": "Latest model",
                                  "categoryCode": "electronics",
                                  "basePrice": 999.99,
                                  "currency": "USD"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("prod-1"))
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    @WithMockUser(authorities = "product:write")
    void deleteProduct_withoutDeletePermission_returnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/products/prod-1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"product:write", "product:delete"})
    void deleteProduct_withDeletePermission_returnsNoContent() throws Exception {
        doNothing().when(productService).deleteProduct("prod-1");

        mockMvc.perform(delete("/api/v1/admin/products/prod-1"))
                .andExpect(status().isNoContent());
    }
}
