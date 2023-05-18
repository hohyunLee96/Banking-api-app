package nl.inholland.bankingapi.model.dto;

public record TransactionGET_DTO(long transactionId, String fromIban, String toIban, double amount, String type) {

}
