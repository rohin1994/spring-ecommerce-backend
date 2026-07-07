package com.ecommerce.order_service.controller;

import com.ecommerce.common.exception.GlobalExceptionHandler;
import com.ecommerce.common.security.SecurityConstants;
import com.ecommerce.order_service.model.dto.request.AddCartItemRequest;
import com.ecommerce.order_service.model.dto.response.CartItemResponse;
import com.ecommerce.order_service.model.dto.response.CartResponse;
import com.ecommerce.order_service.security.UserHeaderFilter;
import com.ecommerce.order_service.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CartController.class)
@Import({UserHeaderFilter.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
class CartControllerTest {

    private static final String USER_ID = "user-789";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    void getCart_withUserHeader_returnsOk() throws Exception {
        CartResponse cart = CartResponse.builder()
                .items(List.of(CartItemResponse.builder().productId("prod-1").quantity(2).build()))
                .build();
        when(cartService.getCart(USER_ID)).thenReturn(cart);

        mockMvc.perform(get("/api/v1/cart")
                        .header(SecurityConstants.HEADER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productId").value("prod-1"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void addCartItem_withUserHeader_returnsCreated() throws Exception {
        CartResponse cart = CartResponse.builder()
                .items(List.of(CartItemResponse.builder().productId("prod-2").quantity(1).build()))
                .build();
        when(cartService.addItem(eq(USER_ID), any(AddCartItemRequest.class))).thenReturn(cart);

        mockMvc.perform(post("/api/v1/cart/items")
                        .header(SecurityConstants.HEADER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": "prod-2",
                                  "quantity": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items[0].productId").value("prod-2"));
    }

    @Test
    void removeCartItem_withUserHeader_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/cart/items/{productId}", "prod-3")
                        .header(SecurityConstants.HEADER_USER_ID, USER_ID))
                .andExpect(status().isNoContent());

        verify(cartService).removeItem(USER_ID, "prod-3");
    }

    @Test
    void getCart_withoutUserHeader_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isUnauthorized());
    }
}
