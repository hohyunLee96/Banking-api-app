package nl.inholland.bankingapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private int userId;
    private String IBAN;
    private double balance;
    private double absoluteLimit;
    private AccountType accountType;
}
