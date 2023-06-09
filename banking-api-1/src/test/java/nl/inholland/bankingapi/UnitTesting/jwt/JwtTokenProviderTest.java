package nl.inholland.bankingapi.UnitTesting.jwt;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import nl.inholland.bankingapi.jwt.JwtKeyProvider;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.security.PrivateKey;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
//    private String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSIsImF1dGgiOiJST0xFX0NVU1RPTUVSIiwiaWF0IjoxNjg2MzEyNzI3LCJleHAiOjE2ODYzMTI3Mjd9.JV6aa-o-oswaszqF2-eAMJAr_oWHF0v1gIyp3hE-WS9hPB2KHC0yHAWrGUbQwEEHIg09M8m7VihUS3S_nXB-35aCQTTRKJQrmkt5Pb-zlOZdxBxkZHgoh_KV-93eUpdzy7qSXYqKEF2n4Biqt1Q1zz_Lks6ykZ0bHxfu7LNnGK8bdqiqbIjlnR5AWHbESosBk9WUM3Gygqe3pVfI_Br4XvMHP2it6-I1K5Evw-xirSepj6NshoO4q6h9whtEssm8jJn542UbbBA658DU9W_sSNMc8ySfyt7xSZDl6-LKvsSKHc836-u3ywgR-Aq73fW6vOhdVjsPPbC1rb1ZpsC1Ow";
    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtKeyProvider jwtKeyProvider;
    private JwtParser jwtParser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, jwtKeyProvider);
        jwtParser = mock(JwtParser.class);
    }

    @Test
    void createToken_ShouldReturnValidToken() {
        // Arrange
        String username = "john@example.com";
        UserType userType = UserType.ROLE_CUSTOMER;

        // Mock the private key
        PrivateKey privateKey = mock(PrivateKey.class);
        when(jwtKeyProvider.getPrivateKey()).thenReturn(privateKey);

        // Act
        String token = jwtTokenProvider.createToken(username, userType);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getAuthentication_WithValidToken_ShouldReturnAuthentication() {
        // Arrange
//        String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSIsImF1dGgiOiJST0xFX0NVU1RPTUVSIiwiaWF0IjoxNjg2MzAwMTMxLCJleHAiOjE2ODYzMDAxMzF9.BKx5_i-hpC11TEKdPKZS2aopH9pdrcTDKuXhlYUvfnQ9iw2vG0mb-m95ZMrQ4YC3ZYTTyXvRrgjSUBjUmkPvNwdQ0lMUlrmOTCgnqWPQAXzI91oI3r-9igKpl9vV8uqBk76P7_3Xy3R-zFgT5jVQVJAruCCr5u6HjEQgTAMwEPkLFd8re4IOX4kL0u-Wd-FrhxdqO3GhHscJsJ9uapRraWGy5CDMBMJX15hNCifmsC_ex7i_r17f4O_JLzJk91fnKIkh2OwLOQeW1p3EQls6_O8wIZ1BGoaXZghCdSCd8lrnP8wHu4JKJ9ti5390XR1wMOfr4aoEbngn-b8OoR0Y-w";
        UserDetails userDetails = new User("user@email.com", "1234", Collections.emptyList());

        // Mock the private key
        PrivateKey privateKey = mock(PrivateKey.class);
        when(jwtKeyProvider.getPrivateKey()).thenReturn(privateKey);

        // Mock the user details service
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

        // Act
        Authentication authentication = jwtTokenProvider.getAuthentication(bearerToken());

        // Assert
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        assertEquals("", authentication.getCredentials());
        assertEquals(userDetails.getAuthorities(), authentication.getAuthorities());
    }

    @Test
    void validateToken_WithValidToken_ShouldNotThrowException() {
        // Arrange
        String token = "valid-token";

        // Mock the private key
        PrivateKey privateKey = mock(PrivateKey.class);
        when(jwtKeyProvider.getPrivateKey()).thenReturn(privateKey);

        // Act and Assert
        assertDoesNotThrow(() -> jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_WithExpiredToken_ShouldThrowException() {
        // Arrange
        String token = "expired-token";

        // Mock the private key
        PrivateKey privateKey = mock(PrivateKey.class);
        when(jwtKeyProvider.getPrivateKey()).thenReturn(privateKey);

        // Throw an exception when parsing claims
        when(jwtParser.parseClaimsJws(token)).thenThrow(ExpiredJwtException.class);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> jwtTokenProvider.validateToken(token));
    }
    public String bearerToken(){
        return "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSIsImF1dGgiOiJST0xFX0NVU1RPTUVSIiwiaWF0IjoxNjg2MzEyNzI3LCJleHAiOjE2ODYzMTI3Mjd9.JV6aa-o-oswaszqF2-eAMJAr_oWHF0v1gIyp3hE-WS9hPB2KHC0yHAWrGUbQwEEHIg09M8m7VihUS3S_nXB-35aCQTTRKJQrmkt5Pb-zlOZdxBxkZHgoh_KV-93eUpdzy7qSXYqKEF2n4Biqt1Q1zz_Lks6ykZ0bHxfu7LNnGK8bdqiqbIjlnR5AWHbESosBk9WUM3Gygqe3pVfI_Br4XvMHP2it6-I1K5Evw-xirSepj6NshoO4q6h9whtEssm8jJn542UbbBA658DU9W_sSNMc8ySfyt7xSZDl6-LKvsSKHc836-u3ywgR-Aq73fW6vOhdVjsPPbC1rb1ZpsC1Ow";
    }
}