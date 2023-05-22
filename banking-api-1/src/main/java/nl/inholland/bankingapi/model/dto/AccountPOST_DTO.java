package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.User;

public class AccountPOST_DTO {

    private User user;

    private String IBAN;
    private double balance;
    private double absoluteLimit;
    private String accountType;

    public AccountPOST_DTO(){

    }
    public AccountPOST_DTO(User user, String IBAN, String balance, String absoluteLimit, String accountType){
        this.user = user;
        this.IBAN = IBAN;
        this.balance = Double.parseDouble(balance);
        this.absoluteLimit = Double.parseDouble(absoluteLimit);
        this.accountType = accountType;
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
