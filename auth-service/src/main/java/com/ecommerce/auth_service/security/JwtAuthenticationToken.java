package com.ecommerce.auth_service.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtClaims claims;

    public JwtAuthenticationToken(JwtClaims claims) {
        super(claims.permissions().stream().map(SimpleGrantedAuthority::new).toList());
        this.claims = claims;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return claims;
    }

    public JwtClaims getClaims() {
        return claims;
    }

    public String getSubjectId() {
        return claims.subject();
    }

    public String getAudience() {
        return claims.audience();
    }

    public String getEmail() {
        return claims.email();
    }

    public List<String> getRoles() {
        return claims.roles();
    }

    public List<String> getPermissions() {
        return claims.permissions();
    }
}
