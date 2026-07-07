package com.ecommerce.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode errorCode;

    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST, ErrorCode.BUSINESS_ERROR);
    }

    public BusinessException(String message, HttpStatus status) {
        this(message, status, mapStatus(status));
    }

    public BusinessException(String message, HttpStatus status, ErrorCode errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    private static ErrorCode mapStatus(HttpStatus status) {
        if (status == HttpStatus.NOT_FOUND) {
            return ErrorCode.NOT_FOUND;
        }
        if (status == HttpStatus.FORBIDDEN) {
            return ErrorCode.FORBIDDEN;
        }
        return ErrorCode.BUSINESS_ERROR;
    }
}
