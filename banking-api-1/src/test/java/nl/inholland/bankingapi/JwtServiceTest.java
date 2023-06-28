package nl.inholland.bankingapi;


import nl.inholland.bankingapi.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void testTokenGeneration() {
        JwtService jwtService = new JwtService();

        // Create a dummy user details object
        UserDetails userDetails = User.builder()
                .username("john.doe")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        // Generate the token
        String token = jwtService.generateToken(userDetails);

        // Verify that the token is not null or empty
        assertTrue(token != null && !token.isEmpty());
    }
}
