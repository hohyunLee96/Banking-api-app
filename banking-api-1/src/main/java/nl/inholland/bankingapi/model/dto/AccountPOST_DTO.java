package nl.inholland.bankingapi.model.dto;

public class AccountPOST_DTO {

    private Long userId;

    private String IBAN;
    private double balance;
    private double absoluteLimit;
    private String accountType;

    public AccountPOST_DTO(){

    }
    public AccountPOST_DTO(String IBAN, String balance, String absoluteLimit, String accountType){
        this.IBAN = IBAN;
        this.balance = Double.parseDouble(balance);
        this.absoluteLimit = Double.parseDouble(absoluteLimit);
        this.accountType = accountType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
