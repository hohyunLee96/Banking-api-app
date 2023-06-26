package nl.inholland.bankingapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
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
    @Column
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
        this.timestamp = timestamp;
        this.performingUser = performingUser;
    }

    public void setAmount(double amount) {
        if(amount <= 0) throw new IllegalArgumentException("Amount cannot be negative or zero");
        this.amount = amount;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        if(timestamp == null) throw new IllegalArgumentException("Timestamp cannot be null");
        this.timestamp = timestamp;
    }

    public void setType(TransactionType type) {
        if(type == null) throw new IllegalArgumentException("Type cannot be null");
        this.type = type;
    }
    public void setPerformingUser(User performingUser) {
        if(performingUser == null) throw new IllegalArgumentException("User cannot be null");
        this.performingUser = performingUser;
    }
    public void setFromIban(Account fromIban) {
        if(fromIban == null) throw new IllegalArgumentException("FromIban cannot be null");
        this.fromIban = fromIban;
    }
    public void setToIban(Account toIban) {
        if(toIban == null) throw new IllegalArgumentException("ToIban cannot be null");
        this.toIban = toIban;
    }

}
