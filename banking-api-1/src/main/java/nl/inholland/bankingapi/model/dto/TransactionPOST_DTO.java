package nl.inholland.bankingapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import nl.inholland.bankingapi.model.TransactionType;

public record TransactionPOST_DTO(@NotBlank String fromIban, String toIban, Double amount, TransactionType type, Integer performingUser) {

}
