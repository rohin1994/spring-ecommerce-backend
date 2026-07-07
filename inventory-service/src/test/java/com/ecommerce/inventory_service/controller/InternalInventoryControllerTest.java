package com.ecommerce.inventory_service.controller;

import com.ecommerce.common.exception.GlobalExceptionHandler;
import com.ecommerce.inventory_service.model.dto.request.InventoryQuantityRequest;
import com.ecommerce.inventory_service.model.dto.response.InventoryResponse;
import com.ecommerce.inventory_service.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InternalInventoryController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class InternalInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @Test
    void reserve_whenInsufficientStock_returnsConflict() throws Exception {
        when(inventoryService.reserve(eq("prod-low-stock"), any(InventoryQuantityRequest.class)))
                .thenThrow(new com.ecommerce.common.exception.BusinessException(
                        "Only 2 items available.",
                        org.springframework.http.HttpStatus.CONFLICT
                ));

        mockMvc.perform(post("/internal/v1/inventory/{productId}/reserve", "prod-low-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 5
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Only 2 items available."));
    }

    @Test
    void reserve_whenStockAvailable_returnsOk() throws Exception {
        InventoryResponse response = InventoryResponse.builder()
                .productId("prod-ok")
                .quantity(10)
                .reserved(3)
                .available(7)
                .build();
        when(inventoryService.reserve(eq("prod-ok"), any(InventoryQuantityRequest.class))).thenReturn(response);

        mockMvc.perform(post("/internal/v1/inventory/{productId}/reserve", "prod-ok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("prod-ok"))
                .andExpect(jsonPath("$.available").value(7));
    }
}
