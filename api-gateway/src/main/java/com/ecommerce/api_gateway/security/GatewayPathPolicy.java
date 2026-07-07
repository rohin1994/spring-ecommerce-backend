package com.ecommerce.api_gateway.security;

import jakarta.servlet.http.HttpServletRequest;

public enum GatewayPathPolicy {
    PUBLIC,
    CUSTOMER,
    ADMIN;

    public static GatewayPathPolicy forRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/actuator")) {
            return PUBLIC;
        }
        if (path.startsWith("/api/v1/auth/")) {
            return PUBLIC;
        }
        if ("/api/v1/admin/auth/login".equals(path) || "/api/v1/admin/auth/refresh".equals(path)) {
            return PUBLIC;
        }
        if ("GET".equalsIgnoreCase(method)
                && (path.startsWith("/api/v1/products") || path.startsWith("/api/v1/categories"))) {
            return PUBLIC;
        }
        if (path.startsWith("/api/v1/admin/")) {
            return ADMIN;
        }
        if (path.startsWith("/api/v1/users/")
                || path.startsWith("/api/v1/cart/")
                || path.startsWith("/api/v1/orders/")) {
            return CUSTOMER;
        }
        return PUBLIC;
    }
}
