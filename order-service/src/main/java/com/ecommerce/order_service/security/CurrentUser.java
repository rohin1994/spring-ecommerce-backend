package com.ecommerce.order_service.security;

import com.ecommerce.common.exception.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class CurrentUser {

    public static final String USER_ID_ATTRIBUTE = "currentUserId";

    private CurrentUser() {
    }

    public static String requireUserId() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            throw new IllegalStateException("No request context available");
        }
        Object userId = servletAttributes.getRequest().getAttribute(USER_ID_ATTRIBUTE);
        if (userId == null || userId.toString().isBlank()) {
            throw new BusinessException(
                    "Please sign in or create an account to continue.",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.AUTH_REQUIRED
            );
        }
        return userId.toString();
    }
}
