package com.ecommerce.product_service.controller.admin;

import com.ecommerce.product_service.config.SecurityConfig;
import com.ecommerce.product_service.config.SecurityExceptionHandler;
import com.ecommerce.product_service.model.dto.request.DiscountCreateRequest;
import com.ecommerce.product_service.model.dto.response.DiscountResponse;
import com.ecommerce.product_service.model.enums.DiscountType;
import com.ecommerce.product_service.security.AdminPermissionFilter;
import com.ecommerce.product_service.service.DiscountService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminDiscountController.class)
@Import({SecurityConfig.class, AdminPermissionFilter.class, GlobalExceptionHandler.class, SecurityExceptionHandler.class})
class AdminDiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiscountService discountService;

    @Test
    @WithMockUser(authorities = "discount:read")
    void getAllDiscounts_withReadPermission_returnsDiscounts() throws Exception {
        DiscountResponse discount = new DiscountResponse(
                "disc-1",
                "SUMMER10",
                DiscountType.PERCENTAGE,
                new BigDecimal("10"),
                List.of("prod-1"),
                List.of("electronics"),
                null,
                null,
                true
        );
        when(discountService.getAllDiscounts()).thenReturn(List.of(discount));

        mockMvc.perform(get("/api/v1/admin/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("SUMMER10"))
                .andExpect(jsonPath("$[0].type").value("PERCENTAGE"));
    }

    @Test
    @WithMockUser(authorities = "discount:write")
    void createDiscount_withWritePermission_returnsCreated() throws Exception {
        DiscountResponse created = new DiscountResponse(
                "disc-1",
                "SUMMER10",
                DiscountType.PERCENTAGE,
                new BigDecimal("10"),
                List.of("prod-1"),
                List.of(),
                null,
                null,
                true
        );
        when(discountService.createDiscount(any(DiscountCreateRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/admin/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "SUMMER10",
                                  "type": "PERCENTAGE",
                                  "value": 10,
                                  "productIds": ["prod-1"],
                                  "active": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("SUMMER10"));
    }

    @Test
    @WithMockUser(authorities = "discount:read")
    void createDiscount_withoutWritePermission_returnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/admin/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "SUMMER10",
                                  "type": "PERCENTAGE",
                                  "value": 10,
                                  "active": true
                                }
                                """))
                .andExpect(status().isForbidden());
    }
}
