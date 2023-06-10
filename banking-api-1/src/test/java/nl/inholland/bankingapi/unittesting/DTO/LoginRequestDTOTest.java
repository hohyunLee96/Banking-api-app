package nl.inholland.bankingapi.unittesting.DTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nl.inholland.bankingapi.model.dto.LoginRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.Assert.*;

class LoginRequestDTOTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createLoginRequestDTO_ShouldSetEmailAndPassword() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        // Act
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);

        // Assert
        Assertions.assertEquals(email, loginRequestDTO.email());
        Assertions.assertEquals(password, loginRequestDTO.password());
    }

    //helps in validating that the class handles null inputs correctly and does not result in unexpected behavior or exceptions.
    @Test
    void createLoginRequestDTO_WithoutParameters_ShouldSetEmailAndPasswordToNull() {
        // Arrange
        String email = null;
        String password = null;

        // Act
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);

        // Assert
        Assertions.assertEquals(email, loginRequestDTO.email());
        Assertions.assertEquals(password, loginRequestDTO.password());
    }

    @Test
    void createLoginRequestDTO_WithInvalidEmail_ShouldResultInAConstraintViolationException() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("notAnEmail", "password");

        Set<ConstraintViolation<LoginRequestDTO>> violations = this.validator.validate(loginRequestDTO);
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("Email is invalid.", errorMessage);
    }

    @Test
    void createLoginRequestDTO_WithAnEmailAndPassword_ShouldResultInAValidObject() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("email@email.com", "password");
        Assertions.assertNotNull(loginRequestDTO);
    }

    @Test
    void createLoginRequestDTO_WithoutAnEmail_ShouldResultInAConstraintViolationException() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("", "password");

        Set<ConstraintViolation<LoginRequestDTO>> violations = this.validator.validate(loginRequestDTO);
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("Email is required.", errorMessage);
    }

    @Test
    void createLoginRequestDTO_WithoutAPassword_ShouldResultInAConstraintViolationException() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("email@email.com", "");

        Set<ConstraintViolation<LoginRequestDTO>> violations = this.validator.validate(loginRequestDTO);
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("Password is required.", errorMessage);
    }

}
