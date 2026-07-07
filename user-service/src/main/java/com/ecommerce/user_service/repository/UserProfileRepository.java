package com.ecommerce.user_service.repository;

import com.ecommerce.user_service.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
}
