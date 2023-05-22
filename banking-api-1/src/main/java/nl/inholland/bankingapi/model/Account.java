package nl.inholland.bankingapi.model;

//import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "name"})})
public class Account {

    @Id
    @GeneratedValue(generator = "account_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="account_seq", initialValue = 1)
    private Long accountId;

    @ManyToOne
    @JsonIgnoreProperties({"accounts"})
    private User user;

    private String IBAN;
    private double balance;
    private double absoluteLimit;
    private AccountType accountType;

    public Account(User user, String iban, double balance, double absoluteLimit, String accountType) {
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
