package nl.inholland.bankingapi.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(generator = "account_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="account_seq", initialValue = 1)
    private Long accountId;

//    @ManyToOne
//    @JsonIgnoreProperties({"accounts"})
//    private Long userId;

    private String IBAN;
    private double balance;
    private double absoluteLimit;
    private AccountType accountType;

    public Account(String iban, double balance, double absoluteLimit, String accountType) {
        this.IBAN = iban;
        this.balance = balance;
        this.absoluteLimit = absoluteLimit;
        this.accountType = AccountType.valueOf(accountType);
    }
    public Account() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getAbsoluteLimit() {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(double absoluteLimit) {
        this.absoluteLimit = absoluteLimit;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
