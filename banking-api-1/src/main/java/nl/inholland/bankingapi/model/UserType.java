package nl.inholland.bankingapi.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {
    ROLE_USER,
    ROLE_EMPLOYEE;

    @Override
    public String getAuthority() {
        return name();
    }
}