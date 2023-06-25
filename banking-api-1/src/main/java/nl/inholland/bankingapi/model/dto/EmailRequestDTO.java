package nl.inholland.bankingapi.model.dto;

import jakarta.validation.constraints.NotBlank;

public record EmailRequestDTO(@NotBlank String emailTo, @NotBlank String subject, @NotBlank String body) {
}
