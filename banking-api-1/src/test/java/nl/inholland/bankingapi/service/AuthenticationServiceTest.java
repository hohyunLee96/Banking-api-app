package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.LoginResponseDTO;
import nl.inholland.bankingapi.model.dto.RegisterRequestDTO;
import nl.inholland.bankingapi.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.naming.AuthenticationException;
import java.util.Optional;

import static org.junit.Assert.assertThrows;

@ExtendWith(SpringExtension.class)
class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(userRepository, jwtTokenProvider, bCryptPasswordEncoder);
    }

    @Test
    void register_ShouldSaveUser() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = createRegisterRequestDTO();
        UserType userType = UserType.ROLE_USER;
        String userEmail = "user@email.com";
        User savedUser = createUser(userType, userEmail);
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(savedUser);

        // Act
        authenticationService.register(registerRequestDTO);

        // Assert
        Mockito.verify(userRepository).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnLoginResponseDTO() throws javax.naming.AuthenticationException {
        // Arrange
        UserType userType = UserType.ROLE_USER;
        String password = "password123";
        String userEmail = "user@email.com";
        User user = createUser(userType, userEmail);
        user.setId(1234L); // Set a valid id value for the user
        Mockito.when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        Mockito.when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(true);
        Mockito.when(jwtTokenProvider.createToken(user.getEmail(), user.getUserType())).thenReturn("jwt-token");

        // Act
        LoginResponseDTO loginResponseDTO = authenticationService.login(userEmail, password);

        // Assert
        Assertions.assertNotNull(loginResponseDTO);
        Assertions.assertEquals("jwt-token", loginResponseDTO.jwt());
        Assertions.assertEquals(userEmail, loginResponseDTO.email());
        Assertions.assertEquals(1234L, loginResponseDTO.id()); // Verify the id value
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowAuthenticationException() {
        // Arrange
        String username = "john";
        String password = "invalidPassword";

        // Act and Assert
        assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(username, password);
        });
    }

    @Test
    void login_WithUserTypeUser_ShouldReturnUserLoginResponseDTO() throws javax.naming.AuthenticationException {
        // Arrange
        String userEmail = "user@email.com";
        String password = "user123";
        UserType userType = UserType.ROLE_USER;
        User normalUser = createUser(userType, userEmail);
        normalUser.setId(9012L);
        normalUser.setUserType(UserType.ROLE_USER);
        Mockito.when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(normalUser));
        Mockito.when(bCryptPasswordEncoder.matches(password, normalUser.getPassword())).thenReturn(true);
        Mockito.when(jwtTokenProvider.createToken(normalUser.getEmail(), normalUser.getUserType())).thenReturn("user-jwt-token");

        // Act
        LoginResponseDTO loginResponseDTO = authenticationService.login(userEmail, password);

        // Assert
        Assertions.assertNotNull(loginResponseDTO);
        Assertions.assertEquals("user-jwt-token", loginResponseDTO.jwt());
        Assertions.assertEquals(userEmail, loginResponseDTO.email());
        Assertions.assertEquals(9012L, loginResponseDTO.id());
        Assertions.assertEquals(UserType.ROLE_USER, normalUser.getUserType());
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowAuthenticationException() {
        // Arrange
        String userEmail = "user@email.com";
        String password = "invalidPassword";
        UserType userType = UserType.ROLE_USER;
        User user = createUser(userType, userEmail);
        user.setId(1234L);
        Mockito.when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        Mockito.when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // Act and Assert
        assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(userEmail, password);
        });
    }

    @Test
    void login_WithNonExistingEmail_ShouldThrowAuthenticationException() {
        // Arrange
        String email = "nonexisting@example.com";
        String password = "password123";
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(email, password);
        });
    }

    // Helper methods to create test objects
    private RegisterRequestDTO createRegisterRequestDTO() {
        return new RegisterRequestDTO(
                "test@example.com",
                "password123",
                "John",
                "Doe",
                "1990-01-01",
                "1234 AB",
                "Test Address",
                "Test City",
                "123456789",
                UserType.ROLE_USER,
                1000.0,
                100.0,
                false
        );
    }

    private User createUser(UserType userType, String email) {
        return new User(
                email,
                "hashedPassword",
                "John",
                "Doe",
                "1990-01-01",
                "1234 AB",
                "Test Address",
                "Test City",
                "123456789",
                userType,
                1000.0,
                100.0,
                false
        );
    }

}
