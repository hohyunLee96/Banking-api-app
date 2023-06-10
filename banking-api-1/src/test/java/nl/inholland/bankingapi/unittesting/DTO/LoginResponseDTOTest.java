package nl.inholland.bankingapi.unittesting.DTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nl.inholland.bankingapi.model.dto.LoginResponseDTO;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class LoginResponseDTOTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test //passed
    public void createLoginResponseDTO_WithJwtEmailAndId_ShouldResultInAValidObject() {
        String jwt = "some-jwt";
        String email = "test@example.com";
        long id = 123;

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(jwt, email, id);

        Assertions.assertNotNull(loginResponseDTO);
        Assertions.assertEquals(jwt, loginResponseDTO.jwt());
        Assertions.assertEquals(email, loginResponseDTO.email());
        Assertions.assertEquals(id, loginResponseDTO.id());
    }

    @Test //passed
    public void createLoginResponseDTO_WithNullValues_ShouldSetNullJwtEmailAndId() {
        String jwt = null;
        String email = null;
        long id = 0;

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(jwt, email, id);

        Assertions.assertNotNull(loginResponseDTO);
        Assertions.assertNull(loginResponseDTO.jwt());
        Assertions.assertNull(loginResponseDTO.email());
        Assertions.assertEquals(id, loginResponseDTO.id());
    }

    @Test
    public void createLoginResponseDTO_WithInvalidEmail_ShouldResultInAConstraintViolationException() {
        String bearerToken = "some-jwt";
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(bearerToken, "notAnEmail", 1L);

        Set<ConstraintViolation<LoginResponseDTO>> violations = validator.validate(loginResponseDTO);
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("Email is invalid.", errorMessage);
    }

    @Test
    public void createLoginResponseDTO_WithoutJwt_ShouldResultInAConstraintViolationException() {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(null, "password", 123);

        Set<ConstraintViolation<LoginResponseDTO>> violations = validator.validate(loginResponseDTO);
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("JWT is required.", errorMessage);
    }

    @Test
    public void createLoginResponseDTO_WithoutEmail_ShouldResultInAConstraintViolationException() {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO("jwt", "", 123);

        Set<ConstraintViolation<LoginResponseDTO>> violations = validator.validate(loginResponseDTO);
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("Email is required.", errorMessage);
    }

    @Test
    public void createLoginResponseDTO_WithoutId_ShouldResultInAConstraintViolationException() {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO("jwt", "email@example.com", 0);

        Set<ConstraintViolation<LoginResponseDTO>> violations = validator.validate(loginResponseDTO);
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("ID is required.", errorMessage);
    }
}