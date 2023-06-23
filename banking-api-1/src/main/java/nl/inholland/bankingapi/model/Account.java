package nl.inholland.bankingapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.bankingapi.exception.ApiRequestException;
import org.springframework.http.HttpStatus;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @ManyToOne
    @JsonIgnoreProperties({"accounts"})
    private User user;

    @Column(unique = true)
    private String IBAN;
    private Double balance; // disable setter for balance directly //small double
    private Double absoluteLimit;
    private AccountType accountType;
    private Boolean isActive;

    public Account(User user, String IBAN, Double balance, Double absoluteLimit, AccountType accountType, Boolean isActive) {
        this.user = user;
        this.IBAN = IBAN;
        this.balance = balance;
        this.absoluteLimit = absoluteLimit;
        this.accountType = accountType;
        this.isActive = isActive;
    }
    public void increaseBalance(Double amount){
        this.balance += amount;
    }
    public void decreaseBalance(Double amount){
        this.balance -= amount;
    }
    public void addBalanceWithNewAccount(){
        this.balance = 0.0;
    }
    public void setBalance(Double amount) {
        throw new ApiRequestException("Balance cannot be set directly", HttpStatus.BAD_REQUEST);
    }

//    public Double getBalance() {
//        return balance;
//    }
//
//    public void setBalance(Double amount) {
//        Double minusAmount = getBalance() - amount;
//        Double plusAmount = getBalance() + amount;
//        if (minusAmount.equals(getBalance() - amount)) {
//            this.balance = minusAmount;
//        } else if (plusAmount.equals(getBalance() + amount)) {
//            this.balance = plusAmount;
//        } else if (amount == 0) {
//            this.balance = 0.0;
//        } else {
//            throw new ApiRequestException("Balance cannot be set directly", HttpStatus.BAD_REQUEST);
//        }
//    }
//    public void setBalance(Double amount) {
//        Double newBalance = getBalance() + amount;
//        if (newBalance.equals(amount)) {
//            this.balance = newBalance;
//        } else if (amount == 0) {
//            this.balance = 0.0;
//        } else {
//            throw new ApiRequestException("Balance cannot be set directly", HttpStatus.BAD_REQUEST);
//        }
//    }
}
