package nl.inholland.bankingapi.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.IOException;
import nl.inholland.bankingapi.controller.AuthenticationController;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.LoginResponseDTO;
import nl.inholland.bankingapi.repository.UserRepository;
import nl.inholland.bankingapi.service.AuthenticationService;
import nl.inholland.bankingapi.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private JwtKeyProvider jwtKeyProvider;
    private JwtParser jwtParser;
    UserRepository userRepository;
    LoginResponseDTO loginResponseDTO;
    AuthenticationController authenticationController;
    @Mock
    PrivateKey mockPrivateKey;

    @BeforeEach
    void setUp() {
        // Mock the jwtParser
        jwtParser = mock(JwtParser.class);
        MockitoAnnotations.openMocks(this);
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, jwtKeyProvider);
        jwtParser = mock(JwtParser.class);
        userRepository = mock(UserRepository.class);
        jwtKeyProvider = mock(JwtKeyProvider.class);jwtTokenProvider = new JwtTokenProvider(userDetailsService, jwtKeyProvider);
        ReflectionTestUtils.setField(jwtKeyProvider, "keystore", "inholland.p12");
        ReflectionTestUtils.setField(jwtKeyProvider, "keystorePassword", "123456");
        ReflectionTestUtils.setField(jwtKeyProvider, "keyAlias", "inholland");
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        loginResponseDTO = new LoginResponseDTO("jwt-token", "user@email.com", 1L);
        authenticationController = new AuthenticationController(authenticationService);
    }

    @Test
    void validateToken_WithValidToken_ShouldNotThrowException() {
        // Arrange
        String token = "valid-token";

        // Mock the private key
        when(jwtKeyProvider.getPrivateKey()).thenReturn(mockPrivateKey);

        // Act and Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> jwtTokenProvider.validateToken(token));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Expired or invalid JWT token", exception.getReason());
    }

    @Test //works
    void validateToken_WithExpiredToken_ShouldThrowException() {
        // Arrange
        String token = "expired-token";

        // Mock the private key
        when(jwtKeyProvider.getPrivateKey()).thenReturn(mockPrivateKey);

        // Throw an exception when parsing claims
        when(jwtParser.parseClaimsJws(token)).thenThrow(ExpiredJwtException.class);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> jwtTokenProvider.validateToken(token));
    }

    @Test
    void createToken_ShouldReturnValidToken() throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, java.io.IOException {
        // Arrange
        String username = "user@email.com";
        UserType userType = UserType.ROLE_USER;
        jwtKeyProvider.init(); // Initialize the JwtKeyProvider to load the private key
        UserDetailsServiceImpl userDetailsServiceImplMock = mock(userDetailsService.getClass());
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(userDetailsServiceImplMock, jwtKeyProvider);

        // Act
        String token = jwtTokenProvider.createToken(username, userType);

        // Assert
        assertNotNull(token);
    }

    @Test
    void getAuthentication_WithValidToken_ShouldReturnAuthentication() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);
        JwtKeyProvider jwtKeyProvider = mock(JwtKeyProvider.class);

        // Mock the user details service
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

        // Create the JwtTokenProvider with the mocked dependencies
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, jwtKeyProvider);

        // Generate a valid token
        String token = generateValidToken(); // Implement this method to generate a valid token

        // Act
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // Assert
        assertNotNull(authentication);
        assertSame(userDetails, authentication.getPrincipal());
        assertEquals("", authentication.getCredentials());
        assertEquals(userDetails.getAuthorities(), authentication.getAuthorities());
    }
    private String generateValidToken() {
        // Generate a valid token using your preferred library or approach
        String subject = "user@email.com"; // Replace with the subject of the token

        // Set the expiration time to some future time
        Date expiration = new Date(System.currentTimeMillis() + 3600000); // 1 hour from now

        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        SecretKey key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

}