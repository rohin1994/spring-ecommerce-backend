package com.ecommerce.auth_service.repository;

import com.ecommerce.auth_service.model.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {

    Optional<AdminUser> findByEmailIgnoreCase(String email);
}
