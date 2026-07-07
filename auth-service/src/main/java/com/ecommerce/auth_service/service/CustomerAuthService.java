package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.config.JwtProperties;
import com.ecommerce.auth_service.model.dto.request.LoginRequest;
import com.ecommerce.auth_service.model.dto.request.RefreshRequest;
import com.ecommerce.auth_service.model.dto.request.RegisterRequest;
import com.ecommerce.auth_service.model.dto.response.AuthResponse;
import com.ecommerce.auth_service.model.entity.Customer;
import com.ecommerce.auth_service.model.entity.RefreshToken;
import com.ecommerce.auth_service.model.enums.TokenOwnerType;
import com.ecommerce.auth_service.repository.CustomerRepository;
import com.ecommerce.auth_service.repository.RefreshTokenRepository;
import com.ecommerce.auth_service.security.JwtService;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class CustomerAuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final CustomerRepository customerRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public CustomerAuthService(CustomerRepository customerRepository,
                               RefreshTokenRepository refreshTokenRepository,
                               PasswordEncoder passwordEncoder,
                               JwtService jwtService,
                               JwtProperties jwtProperties) {
        this.customerRepository = customerRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (customerRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("An account with this email already exists.", HttpStatus.CONFLICT);
        }

        Customer customer = Customer.builder()
                .email(request.email().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .createdAt(Instant.now())
                .build();

        customerRepository.save(customer);
        return issueTokens(customer);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Customer customer = customerRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> invalidCredentials());

        if (!passwordEncoder.matches(request.password(), customer.getPasswordHash())) {
            throw invalidCredentials();
        }

        return issueTokens(customer);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = findValidRefreshToken(request.refreshToken(), TokenOwnerType.CUSTOMER);
        Customer customer = customerRepository.findById(stored.getSubjectId())
                .orElseThrow(() -> invalidCredentials());

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(customer);
    }

    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(request.refreshToken()))
                .filter(token -> token.getOwnerType() == TokenOwnerType.CUSTOMER)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private AuthResponse issueTokens(Customer customer) {
        long accessSeconds = jwtProperties.getAccessTokenExpirationMinutes() * 60;
        String accessToken = jwtService.createCustomerAccessToken(
                customer.getId().toString(),
                customer.getEmail(),
                accessSeconds
        );
        String refreshToken = createRefreshToken(customer.getId(), TokenOwnerType.CUSTOMER);
        return AuthResponse.of(accessToken, refreshToken, accessSeconds);
    }

    private String createRefreshToken(UUID subjectId, TokenOwnerType ownerType) {
        refreshTokenRepository.deleteBySubjectIdAndOwnerType(subjectId, ownerType);

        String rawToken = generateRawToken();
        RefreshToken refreshToken = RefreshToken.builder()
                .subjectId(subjectId)
                .ownerType(ownerType)
                .tokenHash(hashToken(rawToken))
                .expiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshTokenExpirationDays() * 24 * 60 * 60))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    private RefreshToken findValidRefreshToken(String rawToken, TokenOwnerType ownerType) {
        RefreshToken token = refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(rawToken))
                .filter(t -> t.getOwnerType() == ownerType)
                .orElseThrow(() -> new BusinessException(
                        "Please sign in or create an account to continue.",
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.AUTH_REQUIRED
                ));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            throw new BusinessException(
                    "Please sign in or create an account to continue.",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.AUTH_REQUIRED
            );
        }
        return token;
    }

    private BusinessException invalidCredentials() {
        return new BusinessException(
                "Invalid email or password.",
                HttpStatus.UNAUTHORIZED,
                ErrorCode.AUTH_REQUIRED
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
