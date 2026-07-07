package com.ecommerce.api_gateway.filter;

import com.ecommerce.api_gateway.security.GatewayPathPolicy;
import com.ecommerce.api_gateway.security.JwtValidator;
import com.ecommerce.common.dto.ApiProblemDetail;
import com.ecommerce.common.exception.ErrorCode;
import com.ecommerce.common.filter.TraceIdFilter;
import com.ecommerce.common.security.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtValidator jwtValidator, ObjectMapper objectMapper) {
        this.jwtValidator = jwtValidator;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        GatewayPathPolicy policy = GatewayPathPolicy.forRequest(request);
        if (policy == GatewayPathPolicy.PUBLIC) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractBearerToken(request);
        if (token == null) {
            writeProblem(response, request, policy == GatewayPathPolicy.ADMIN
                    ? ErrorCode.ADMIN_AUTH_REQUIRED
                    : ErrorCode.AUTH_REQUIRED, HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Claims claims;
        try {
            claims = jwtValidator.parseAndValidate(token);
        } catch (JwtException | IllegalArgumentException ex) {
            writeProblem(response, request, policy == GatewayPathPolicy.ADMIN
                    ? ErrorCode.ADMIN_AUTH_REQUIRED
                    : ErrorCode.AUTH_REQUIRED, HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String requiredAudience = policy == GatewayPathPolicy.ADMIN
                ? SecurityConstants.AUD_ADMIN
                : SecurityConstants.AUD_ECOMMERCE;
        if (!jwtValidator.hasAudience(claims, requiredAudience)) {
            writeProblem(response, request, policy == GatewayPathPolicy.ADMIN
                    ? ErrorCode.ADMIN_FORBIDDEN
                    : ErrorCode.FORBIDDEN, HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        HeaderEnrichingRequestWrapper enrichedRequest = new HeaderEnrichingRequestWrapper(request);
        if (policy == GatewayPathPolicy.ADMIN) {
            enrichedRequest.setHeader(SecurityConstants.HEADER_ADMIN_ID, claims.getSubject());
            enrichedRequest.setHeader(SecurityConstants.HEADER_ADMIN_PERMISSIONS, formatPermissions(claims));
        } else {
            enrichedRequest.setHeader(SecurityConstants.HEADER_USER_ID, claims.getSubject());
        }

        filterChain.doFilter(enrichedRequest, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7).trim();
        return token.isEmpty() ? null : token;
    }

    private String formatPermissions(Claims claims) {
        Object permissions = claims.get("permissions");
        if (permissions instanceof Collection<?> values) {
            return values.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        }
        if (permissions instanceof String value && !value.isBlank()) {
            return value;
        }
        return "";
    }

    private void writeProblem(HttpServletResponse response, HttpServletRequest request, ErrorCode errorCode, int status)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        ApiProblemDetail problem = new ApiProblemDetail(
                errorCode.type(),
                errorCode.title(),
                status,
                errorCode.title(),
                request.getRequestURI(),
                MDC.get(TraceIdFilter.TRACE_ID),
                null
        );
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
