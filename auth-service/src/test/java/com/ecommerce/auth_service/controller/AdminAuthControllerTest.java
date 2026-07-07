package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.config.JwtProperties;
import com.ecommerce.auth_service.config.SecurityConfig;
import com.ecommerce.auth_service.model.dto.response.AuthResponse;
import com.ecommerce.auth_service.security.JwtAuthenticationFilter;
import com.ecommerce.auth_service.security.JwtService;
import com.ecommerce.auth_service.service.AdminAuthService;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ErrorCode;
import com.ecommerce.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminAuthController.class)
@Import({SecurityConfig.class, JwtService.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
class AdminAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminAuthService adminAuthService;

    @Test
    void login_returnsAdminTokensWithRolesAndPermissions() throws Exception {
        AuthResponse response = AuthResponse.of(
                "admin-access",
                "admin-refresh",
                900,
                List.of("CATALOG_MANAGER"),
                List.of("product:read", "product:write", "category:write")
        );
        when(adminAuthService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "catalog@mydomain.com",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("admin-access"))
                .andExpect(jsonPath("$.roles[0]").value("CATALOG_MANAGER"))
                .andExpect(jsonPath("$.permissions[0]").value("product:read"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(adminAuthService.login(any())).thenThrow(new BusinessException(
                "Invalid email or password.",
                HttpStatus.UNAUTHORIZED,
                ErrorCode.ADMIN_AUTH_REQUIRED
        ));

        mockMvc.perform(post("/api/v1/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "catalog@mydomain.com",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Admin sign-in required."))
                .andExpect(jsonPath("$.detail").value("Invalid email or password."));
    }

    @Test
    void me_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/me"))
                .andExpect(status().isUnauthorized());
    }
}
