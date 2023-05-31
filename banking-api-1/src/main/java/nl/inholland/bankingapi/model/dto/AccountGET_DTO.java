package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.AccountType;
import org.apache.catalina.User;

public record AccountGET_DTO (String user, String IBAN, Double balance, Double absoluteLimit, AccountType accountType){
}
