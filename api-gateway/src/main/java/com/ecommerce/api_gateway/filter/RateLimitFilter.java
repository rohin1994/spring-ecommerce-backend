package com.ecommerce.api_gateway.filter;

import com.ecommerce.common.dto.ApiProblemDetail;
import com.ecommerce.common.exception.ErrorCode;
import com.ecommerce.common.filter.TraceIdFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory token bucket rate limiter (10 requests/sec per client IP).
 * For distributed deployments, replace with Bucket4j + Redis.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int REQUESTS_PER_SECOND = 10;

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String clientIp = resolveClientIp(request);
        TokenBucket bucket = buckets.computeIfAbsent(clientIp, ignored -> new TokenBucket(REQUESTS_PER_SECOND));

        if (!bucket.tryConsume()) {
            writeTooManyRequests(response, request.getRequestURI());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeTooManyRequests(HttpServletResponse response, String path) throws IOException {
        ApiProblemDetail problem = new ApiProblemDetail(
                ErrorCode.TOO_MANY_REQUESTS.type(),
                ErrorCode.TOO_MANY_REQUESTS.title(),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Rate limit exceeded. Please try again in a moment.",
                path,
                traceId(),
                null
        );
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String traceId() {
        String traceId = MDC.get(TraceIdFilter.TRACE_ID);
        return traceId != null ? traceId : "unknown";
    }

    static final class TokenBucket {

        private final int capacity;
        private final long refillIntervalNanos;
        private double tokens;
        private long lastRefillNanos;

        TokenBucket(int capacity) {
            this.capacity = capacity;
            this.tokens = capacity;
            this.refillIntervalNanos = 1_000_000_000L;
            this.lastRefillNanos = System.nanoTime();
        }

        synchronized boolean tryConsume() {
            refill();
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            long elapsed = now - lastRefillNanos;
            if (elapsed <= 0) {
                return;
            }
            double refillAmount = (elapsed / (double) refillIntervalNanos) * capacity;
            tokens = Math.min(capacity, tokens + refillAmount);
            lastRefillNanos = now;
        }
    }
}
