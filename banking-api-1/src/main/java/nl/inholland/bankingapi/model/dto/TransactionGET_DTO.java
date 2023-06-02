package nl.inholland.bankingapi.model.dto;


import nl.inholland.bankingapi.model.TransactionType;

import java.time.LocalDateTime;

public record TransactionGET_DTO(long transactionId, String fromIban, String toIban, double amount, TransactionType type,
                                 LocalDateTime timeStamp, long performingUserId) {

}
