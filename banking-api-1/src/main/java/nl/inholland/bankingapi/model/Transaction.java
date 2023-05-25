package nl.inholland.bankingapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromIban;
    private String toIban;
    private double amount;
    private LocalDateTime timestamp;
    private TransactionType type;

    public Transaction(String fromIban, String toIban, double amount, LocalDateTime timestamp, TransactionType type, User user) {
        this.fromIban = fromIban;
        this.toIban = toIban;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
}
