package com.ecommerce.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiProblemDetail(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        String traceId,
        Map<String, String> errors
) {
}
