package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.TransactionType;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record TransactionPOST_DTO(@NotBlank String fromIban, String toIban, double amount, TransactionType type) {
}
