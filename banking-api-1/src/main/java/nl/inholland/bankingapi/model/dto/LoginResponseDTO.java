package nl.inholland.bankingapi.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginResponseDTO(
        @NotBlank(message = "JWT is required.") String jwt,
        @NotBlank(message = "Email is required.") @Email(message = "Email is invalid.") String email,
        long id
) {
}

