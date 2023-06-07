package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.TransactionType;

public record TransactionWithdrawDTO(String iban, Double amount, TransactionType type) {
}
