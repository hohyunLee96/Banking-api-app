package nl.inholland.bankingapi.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {
    ROLE_USER,
    ROLE_EMPLOYEE,
    ROLE_BOTH;

    @Override
    public String getAuthority() {
        return name();
    }
}