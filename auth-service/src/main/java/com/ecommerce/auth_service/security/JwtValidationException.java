package com.ecommerce.auth_service.security;

public class JwtValidationException extends RuntimeException {

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
