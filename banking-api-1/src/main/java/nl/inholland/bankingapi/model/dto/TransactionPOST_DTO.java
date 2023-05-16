package nl.inholland.bankingapi.model.dto;

public record TransactionPOST_DTO(String fromIban, String toIban, double amount, String type) {
}
