package nl.inholland.bankingapi.model.dto;


import jakarta.validation.constraints.NotBlank;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.User;
import java.time.LocalDateTime;

public record TransactionPOST_DTO(@NotBlank Account fromIban, Account toIban, double amount, TransactionType type, LocalDateTime timestamp, User performingUser) {

}
