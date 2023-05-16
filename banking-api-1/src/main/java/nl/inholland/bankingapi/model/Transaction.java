package nl.inholland.bankingapi.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

        public Transaction(String fromIban, String toIban, double amount, TransactionType type) {
                this.fromIban = fromIban;
                this.toIban = toIban;
                this.amount = amount;
                this.type = type;
                this.timestamp = LocalDateTime.now();
        }


}
