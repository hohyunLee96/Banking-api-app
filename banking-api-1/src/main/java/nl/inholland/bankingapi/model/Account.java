package nl.inholland.bankingapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(generator = "account_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="account_seq", initialValue = 1)
    private Long accountId;

    @ManyToOne
    @JsonIgnoreProperties({"accounts"})
    private Long userId;

    private String IBAN;
    private double balance;
    private double absoluteLimit;
    private AccountType accountType;
}
