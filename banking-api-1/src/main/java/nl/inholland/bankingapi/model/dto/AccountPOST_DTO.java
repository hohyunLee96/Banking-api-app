package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.User;

public record AccountPOST_DTO(User user, String IBAN, double balance, double absoluteLimit, AccountType accountType) {

}

