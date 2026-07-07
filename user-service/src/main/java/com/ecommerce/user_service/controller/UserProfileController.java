package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.model.dto.request.UserProfileUpdateRequest;
import com.ecommerce.user_service.model.dto.response.UserProfileResponse;
import com.ecommerce.user_service.security.CurrentUser;
import com.ecommerce.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(userProfileService.getProfile(CurrentUser.requireUserId()));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(CurrentUser.requireUserId(), request));
    }
}
