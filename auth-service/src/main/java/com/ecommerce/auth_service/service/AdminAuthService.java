package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.config.JwtProperties;
import com.ecommerce.auth_service.model.dto.request.LoginRequest;
import com.ecommerce.auth_service.model.dto.request.RefreshRequest;
import com.ecommerce.auth_service.model.dto.response.AdminMeResponse;
import com.ecommerce.auth_service.model.dto.response.AuthResponse;
import com.ecommerce.auth_service.model.entity.AdminUser;
import com.ecommerce.auth_service.model.entity.Permission;
import com.ecommerce.auth_service.model.entity.RefreshToken;
import com.ecommerce.auth_service.model.entity.Role;
import com.ecommerce.auth_service.model.enums.TokenOwnerType;
import com.ecommerce.auth_service.repository.AdminUserRepository;
import com.ecommerce.auth_service.repository.RefreshTokenRepository;
import com.ecommerce.auth_service.security.JwtAuthenticationToken;
import com.ecommerce.auth_service.security.JwtService;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AdminAuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AdminUserRepository adminUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AdminAuthService(AdminUserRepository adminUserRepository,
                            RefreshTokenRepository refreshTokenRepository,
                            PasswordEncoder passwordEncoder,
                            JwtService jwtService,
                            JwtProperties jwtProperties) {
        this.adminUserRepository = adminUserRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        AdminUser admin = adminUserRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(this::invalidCredentials);

        if (!admin.isActive() || !passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            throw invalidCredentials();
        }

        return issueTokens(admin);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = findValidRefreshToken(request.refreshToken());
        AdminUser admin = adminUserRepository.findById(stored.getSubjectId())
                .orElseThrow(this::invalidCredentials);

        if (!admin.isActive()) {
            throw invalidCredentials();
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(admin);
    }

    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(request.refreshToken()))
                .filter(token -> token.getOwnerType() == TokenOwnerType.ADMIN)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional(readOnly = true)
    public AdminMeResponse me() {
        JwtAuthenticationToken authentication = currentAdminAuthentication();
        AdminUser admin = adminUserRepository.findById(UUID.fromString(authentication.getSubjectId()))
                .orElseThrow(() -> new BusinessException(
                        "Admin sign-in required.",
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.ADMIN_AUTH_REQUIRED
                ));

        List<String> roles = extractRoles(admin);
        List<String> permissions = extractPermissions(admin);
        return new AdminMeResponse(
                admin.getId().toString(),
                admin.getEmail(),
                admin.getName(),
                roles,
                permissions
        );
    }

    private AuthResponse issueTokens(AdminUser admin) {
        List<String> roles = extractRoles(admin);
        List<String> permissions = extractPermissions(admin);
        long accessSeconds = jwtProperties.getAccessTokenExpirationMinutes() * 60;
        String accessToken = jwtService.createAdminAccessToken(
                admin.getId().toString(),
                admin.getEmail(),
                roles,
                permissions,
                accessSeconds
        );
        String refreshToken = createRefreshToken(admin.getId());
        return AuthResponse.of(accessToken, refreshToken, accessSeconds, roles, permissions);
    }

    private List<String> extractRoles(AdminUser admin) {
        return admin.getRoles().stream()
                .map(Role::getCode)
                .sorted()
                .toList();
    }

    private List<String> extractPermissions(AdminUser admin) {
        Set<String> permissions = new LinkedHashSet<>();
        admin.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .forEach(permissions::add);
        List<String> sorted = new ArrayList<>(permissions);
        sorted.sort(Comparator.naturalOrder());
        return sorted;
    }

    private String createRefreshToken(UUID subjectId) {
        refreshTokenRepository.deleteBySubjectIdAndOwnerType(subjectId, TokenOwnerType.ADMIN);

        String rawToken = generateRawToken();
        RefreshToken refreshToken = RefreshToken.builder()
                .subjectId(subjectId)
                .ownerType(TokenOwnerType.ADMIN)
                .tokenHash(hashToken(rawToken))
                .expiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshTokenExpirationDays() * 24 * 60 * 60))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    private RefreshToken findValidRefreshToken(String rawToken) {
        RefreshToken token = refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(rawToken))
                .filter(t -> t.getOwnerType() == TokenOwnerType.ADMIN)
                .orElseThrow(() -> new BusinessException(
                        "Admin sign-in required.",
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.ADMIN_AUTH_REQUIRED
                ));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            throw new BusinessException(
                    "Admin sign-in required.",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.ADMIN_AUTH_REQUIRED
            );
        }
        return token;
    }

    private JwtAuthenticationToken currentAdminAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth;
        }
        throw new BusinessException(
                "Admin sign-in required.",
                HttpStatus.UNAUTHORIZED,
                ErrorCode.ADMIN_AUTH_REQUIRED
        );
    }

    private BusinessException invalidCredentials() {
        return new BusinessException(
                "Invalid email or password.",
                HttpStatus.UNAUTHORIZED,
                ErrorCode.ADMIN_AUTH_REQUIRED
        );
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
