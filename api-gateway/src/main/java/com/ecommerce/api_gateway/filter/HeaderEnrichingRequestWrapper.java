package com.ecommerce.api_gateway.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class HeaderEnrichingRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> additionalHeaders = new HashMap<>();

    HeaderEnrichingRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    void setHeader(String name, String value) {
        additionalHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String value = additionalHeaders.get(name);
        if (value != null) {
            return value;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>(additionalHeaders.keySet());
        Enumeration<String> original = super.getHeaderNames();
        while (original.hasMoreElements()) {
            names.add(original.nextElement());
        }
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (additionalHeaders.containsKey(name)) {
            return Collections.enumeration(Set.of(additionalHeaders.get(name)));
        }
        return super.getHeaders(name);
    }
}
