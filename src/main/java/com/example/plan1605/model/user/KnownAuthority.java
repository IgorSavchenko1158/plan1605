package com.example.plan1605.model.user;

import org.springframework.security.core.GrantedAuthority;

public enum KnownAuthority implements GrantedAuthority {

    ROLE_USER,

    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
