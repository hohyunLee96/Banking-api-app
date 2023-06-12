package nl.inholland.bankingapi.unittesting.DTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nl.inholland.bankingapi.model.dto.LoginResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginResponseDTOTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createLoginResponseDTO_WithJwtEmailAndId_ShouldResultInAValidObject() {
        String jwt = "some-jwt";
        String email = "test@example.com";
        long id = 123;

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(jwt, email, id);

        Assertions.assertNotNull(loginResponseDTO);
        Assertions.assertEquals(jwt, loginResponseDTO.jwt());
        Assertions.assertEquals(email, loginResponseDTO.email());
        Assertions.assertEquals(id, loginResponseDTO.id());
    }

    @Test
    void createLoginResponseDTO_WithNullValues_ShouldSetNullJwtEmailAndId() {
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
    void createLoginResponseDTO_WithInvalidEmail_ShouldResultInAConstraintViolationException() {
        String bearerToken = "some-jwt";
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(bearerToken, "notAnEmail", 1L);

        Set<ConstraintViolation<LoginResponseDTO>> violations = validator.validate(loginResponseDTO);
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("Email invalid", errorMessage);
    }

    @Test
    void createLoginResponseDTO_WithoutJwt_ShouldResultInAConstraintViolationException() {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(null, "email", 1L);

        Set<ConstraintViolation<LoginResponseDTO>> violations = validator.validate(loginResponseDTO);
        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation<LoginResponseDTO> violation : violations) {
            errorMessages.add(violation.getMessage());
        }

        assertTrue(errorMessages.contains("JWT required"));
    }


    @Test
    void createLoginResponseDTO_WithoutEmail_ShouldResultInAConstraintViolationException() {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO("jwt", "", 1L);

        Set<ConstraintViolation<LoginResponseDTO>> violations = validator.validate(loginResponseDTO);
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("Email required", errorMessage);
    }

}
