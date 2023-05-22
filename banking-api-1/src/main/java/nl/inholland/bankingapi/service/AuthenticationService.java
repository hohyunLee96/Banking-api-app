package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.AuthenticationResult;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthenticationService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public AuthenticationResult authenticate(String username, String password) {
        // Retrieve the user from the repository based on the provided username
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (validatePassword(password, user.getPassword())) {
                // Authentication successful
                return new AuthenticationResult(true, user);
            }
        }
        // Authentication failed
        return new AuthenticationResult(false, null);
    }

    public String generateAccessToken(User user) {
        // Generate an access token using the token service
        return tokenService.generateAccessToken(user);
    }

    private boolean validatePassword(String password, String hashedPassword) {
        // Implement password validation logic, e.g., by comparing the provided password with the hashed password
        // You can use libraries like BCrypt or Argon2 for secure password hashing and validation
        // Return true if the password is valid, false otherwise
        return true;
    }
}
