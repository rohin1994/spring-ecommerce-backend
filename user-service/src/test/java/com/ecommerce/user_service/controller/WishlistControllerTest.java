package com.ecommerce.user_service.controller;

import com.ecommerce.common.exception.GlobalExceptionHandler;
import com.ecommerce.common.security.SecurityConstants;
import com.ecommerce.user_service.config.SecurityConfig;
import com.ecommerce.user_service.model.dto.response.WishlistItemResponse;
import com.ecommerce.user_service.security.UserHeaderFilter;
import com.ecommerce.user_service.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WishlistController.class)
@Import({SecurityConfig.class, UserHeaderFilter.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
class WishlistControllerTest {

    private static final String USER_ID = "user-123";
    private static final String PRODUCT_ID = "prod-456";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishlistService wishlistService;

    @Test
    void getWishlistItem_withUserHeader_returnsOk() throws Exception {
        WishlistItemResponse response = WishlistItemResponse.builder()
                .productId(PRODUCT_ID)
                .addedAt(Instant.parse("2026-07-06T10:00:00Z"))
                .build();
        when(wishlistService.getItem(USER_ID, PRODUCT_ID)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me/wishlist/{productId}", PRODUCT_ID)
                        .header(SecurityConstants.HEADER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.addedAt").value("2026-07-06T10:00:00Z"));
    }

    @Test
    void addWishlistItem_withUserHeader_returnsCreated() throws Exception {
        WishlistItemResponse response = WishlistItemResponse.builder()
                .productId(PRODUCT_ID)
                .addedAt(Instant.parse("2026-07-06T10:00:00Z"))
                .build();
        when(wishlistService.addItem(USER_ID, PRODUCT_ID)).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/me/wishlist/{productId}", PRODUCT_ID)
                        .header(SecurityConstants.HEADER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID));
    }

    @Test
    void removeWishlistItem_withUserHeader_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me/wishlist/{productId}", PRODUCT_ID)
                        .header(SecurityConstants.HEADER_USER_ID, USER_ID))
                .andExpect(status().isNoContent());

        verify(wishlistService).removeItem(eq(USER_ID), eq(PRODUCT_ID));
    }

    @Test
    void getWishlistItem_withoutUserHeader_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/wishlist/{productId}", PRODUCT_ID))
                .andExpect(status().isUnauthorized());
    }
}
