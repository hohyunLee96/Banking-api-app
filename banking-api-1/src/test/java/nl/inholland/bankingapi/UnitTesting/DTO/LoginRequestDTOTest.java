package nl.inholland.bankingapi.UnitTesting.DTO;

import nl.inholland.bankingapi.model.dto.LoginRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoginRequestDTOTest {

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

}
