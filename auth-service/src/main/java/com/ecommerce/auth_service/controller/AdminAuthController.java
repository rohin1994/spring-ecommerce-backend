package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.model.dto.request.LoginRequest;
import com.ecommerce.auth_service.model.dto.request.RefreshRequest;
import com.ecommerce.auth_service.model.dto.response.AdminMeResponse;
import com.ecommerce.auth_service.model.dto.response.AuthResponse;
import com.ecommerce.auth_service.service.AdminAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return adminAuthService.login(request);
    }

    @PostMapping("/auth/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return adminAuthService.refresh(request);
    }

    @PostMapping("/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequest request) {
        adminAuthService.logout(request);
    }

    @GetMapping("/me")
    public AdminMeResponse me() {
        return adminAuthService.me();
    }
}
