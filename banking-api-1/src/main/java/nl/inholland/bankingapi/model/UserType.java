package nl.inholland.bankingapi.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {
    user,
    employee;

    @Override
    public String getAuthority() {
        return null;
    }
}