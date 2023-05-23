package nl.inholland.bankingapi.model.dto;


import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.TransactionType;

import jakarta.validation.constraints.NotBlank;
import nl.inholland.bankingapi.model.User;

public record TransactionPOST_DTO(@NotBlank Account fromIban, Account toIban, double amount, TransactionType type, User performingUser) {
}
