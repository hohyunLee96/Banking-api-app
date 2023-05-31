package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.Account;

public record TransactionGET_DTO(long transactionId, Account fromIban, Account toIban, double amount, String type, UserGET_DTO performingUser) {
}
