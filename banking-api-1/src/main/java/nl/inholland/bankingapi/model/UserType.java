package nl.inholland.bankingapi.model;
import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {
    USER("USER"),
    EMPLOYEE("EMPLOYEE");

    private String authority;

    UserType(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
