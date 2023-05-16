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

        private String fromIban;
        private String toIban;
        private double amount;
        private LocalDateTime timestamp;
        private TransactionType type;

}
