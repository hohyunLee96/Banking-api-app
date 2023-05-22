package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.TransactionType;

public record TransactionPOST_DTO(String fromIban, String toIban, double amount, TransactionType type) {
}
