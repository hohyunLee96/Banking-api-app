package nl.inholland.bankingapi.model;

import lombok.Data;

@Data
public record AuthenticationResult(boolean authenticated, User user,
                                   String accessToken, String refreshToken) {

    public boolean isAuthenticated() {
        return authenticated;
    }

    public User getUser() {
        return user;
    }
}

