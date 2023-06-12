package nl.inholland.bankingapi.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginResponseDTO(
        @NotBlank(message = "JWT required") String jwt,
        @NotBlank(message = "Email required") @Email(message = "Email invalid") String email,
        @NotNull(message = "Id required") long id
) {
}

