package com.ecommerce.user_service.service;

import com.ecommerce.user_service.model.dto.request.UserProfileUpdateRequest;
import com.ecommerce.user_service.model.dto.response.UserProfileResponse;

public interface UserProfileService {

    UserProfileResponse getProfile(String userId);

    UserProfileResponse updateProfile(String userId, UserProfileUpdateRequest request);
}
