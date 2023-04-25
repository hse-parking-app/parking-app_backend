package org.hse.parkings.model.employee;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    APP_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
