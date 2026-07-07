package com.ecommerce.common.exception;

import com.ecommerce.common.dto.ApiProblemDetail;
import com.ecommerce.common.filter.TraceIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_BASE_URI = "https://api.mydomain.com/errors/";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiProblemDetail> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Business error traceId={} path={} message={}", traceId(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(problem(
                ex.getErrorCode(),
                ex.getStatus().value(),
                ex.getMessage(),
                request.getRequestURI(),
                null
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(problem(
                ErrorCode.VALIDATION_FAILED,
                HttpStatus.BAD_REQUEST.value(),
                "Please correct the highlighted fields and try again.",
                request.getRequestURI(),
                errors
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiProblemDetail> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled error traceId={} path={}", traceId(), request.getRequestURI(), ex);
        return ResponseEntity.internalServerError().body(problem(
                ErrorCode.INTERNAL_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something went wrong. Reference: " + traceId(),
                request.getRequestURI(),
                null
        ));
    }

    private ApiProblemDetail problem(ErrorCode code, int status, String detail, String instance, Map<String, String> errors) {
        return new ApiProblemDetail(
                ERROR_BASE_URI + code.type(),
                code.title(),
                status,
                detail,
                instance,
                traceId(),
                errors
        );
    }

    private String traceId() {
        String traceId = MDC.get(TraceIdFilter.TRACE_ID);
        return traceId != null ? traceId : "unknown";
    }
}
