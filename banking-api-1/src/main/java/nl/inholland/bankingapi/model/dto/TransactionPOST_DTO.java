package nl.inholland.bankingapi.model.dto;


import jakarta.validation.constraints.NotBlank;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.User;

public record TransactionPOST_DTO(@NotBlank String fromIban, String toIban, Double amount, TransactionType type, long performingUser) {

}
