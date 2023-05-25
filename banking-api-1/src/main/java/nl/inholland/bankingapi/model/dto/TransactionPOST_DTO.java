package nl.inholland.bankingapi.model.dto;


import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.TransactionType;

import java.time.LocalDateTime;

public record TransactionPOST_DTO(Account fromIban, Account toIban, double amount, TransactionType type, LocalDateTime timestamp) {

}
