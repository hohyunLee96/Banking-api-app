package nl.inholland.bankingapi.model;

import jakarta.persistence.*;
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
    @OneToOne
    private Account fromIban;
    @OneToOne
    private Account toIban;
    private double amount;
    private LocalDateTime timestamp;
    private TransactionType type;
    @OneToOne
    private User performingUser;

    public Transaction(Account fromIban, Account toIban, double amount, LocalDateTime timestamp, TransactionType type, User performingUser) {
        this.fromIban = fromIban;
        this.toIban = toIban;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.performingUser = performingUser;
    }

}
