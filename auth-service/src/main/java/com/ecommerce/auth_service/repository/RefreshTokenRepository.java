package com.ecommerce.auth_service.repository;

import com.ecommerce.auth_service.model.entity.RefreshToken;
import com.ecommerce.auth_service.model.enums.TokenOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    void deleteBySubjectIdAndOwnerType(UUID subjectId, TokenOwnerType ownerType);
}
