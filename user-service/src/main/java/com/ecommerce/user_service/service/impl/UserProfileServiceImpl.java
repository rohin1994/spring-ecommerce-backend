package com.ecommerce.user_service.service.impl;

import com.ecommerce.user_service.model.dto.request.UserProfileUpdateRequest;
import com.ecommerce.user_service.model.dto.response.UserProfileResponse;
import com.ecommerce.user_service.model.entity.UserProfile;
import com.ecommerce.user_service.repository.UserProfileRepository;
import com.ecommerce.user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String userId) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseGet(() -> UserProfile.builder().userId(userId).build());
        return toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String userId, UserProfileUpdateRequest request) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseGet(() -> UserProfile.builder().userId(userId).build());

        if (request.getName() != null) {
            profile.setName(request.getName());
        }
        if (request.getEmail() != null) {
            profile.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }

        UserProfile saved = userProfileRepository.save(profile);
        return toResponse(saved);
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .userId(profile.getUserId())
                .name(profile.getName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .build();
    }
}
