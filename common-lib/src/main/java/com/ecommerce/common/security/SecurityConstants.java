package com.ecommerce.common.security;

public final class SecurityConstants {

    public static final String AUD_ECOMMERCE = "ecommerce-app";
    public static final String AUD_ADMIN = "admin-panel";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_ADMIN_ID = "X-Admin-Id";
    public static final String HEADER_ADMIN_PERMISSIONS = "X-Admin-Permissions";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    private SecurityConstants() {
    }
}
