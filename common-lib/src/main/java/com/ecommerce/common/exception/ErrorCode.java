package com.ecommerce.common.exception;

public enum ErrorCode {
    VALIDATION_FAILED("validation-failed", "Validation failed"),
    BUSINESS_ERROR("business-error", "Request could not be completed"),
    NOT_FOUND("not-found", "Resource not found"),
    AUTH_REQUIRED("auth-required", "Please sign in or create an account to continue."),
    ADMIN_AUTH_REQUIRED("admin-auth-required", "Admin sign-in required."),
    FORBIDDEN("forbidden", "You do not have permission to perform this action."),
    ADMIN_FORBIDDEN("admin-forbidden", "This area is for staff accounts only."),
    TOO_MANY_REQUESTS("too-many-requests", "Too many requests"),
    INTERNAL_ERROR("internal-error", "Something went wrong. Please try again later.");

    private final String type;
    private final String title;

    ErrorCode(String type, String title) {
        this.type = type;
        this.title = title;
    }

    public String type() {
        return type;
    }

    public String title() {
        return title;
    }
}
