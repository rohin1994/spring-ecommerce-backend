package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.config.JwtProperties;
import com.ecommerce.auth_service.config.SecurityConfig;
import com.ecommerce.auth_service.model.dto.response.AuthResponse;
import com.ecommerce.auth_service.security.JwtAuthenticationFilter;
import com.ecommerce.auth_service.security.JwtService;
import com.ecommerce.auth_service.service.CustomerAuthService;
import com.ecommerce.common.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerAuthController.class)
@Import({SecurityConfig.class, JwtService.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerAuthService customerAuthService;

    @Test
    void register_returnsCreatedWithTokens() throws Exception {
        AuthResponse response = AuthResponse.of("access-token", "refresh-token", 900);
        when(customerAuthService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "shopper@mydomain.com",
                                  "password": "password123",
                                  "name": "Shopper"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInSeconds").value(900));
    }

    @Test
    void login_returnsOkWithTokens() throws Exception {
        AuthResponse response = AuthResponse.of("customer-access", "customer-refresh", 900);
        when(customerAuthService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "shopper@mydomain.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("customer-access"))
                .andExpect(jsonPath("$.refreshToken").value("customer-refresh"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
}
