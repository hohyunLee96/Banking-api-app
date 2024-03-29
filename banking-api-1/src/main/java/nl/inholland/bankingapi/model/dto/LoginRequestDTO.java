package nl.inholland.bankingapi.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message="Email required") @Email(message="Invalid email") String email,
        @NotBlank(message="Password required") String password
) {
}
