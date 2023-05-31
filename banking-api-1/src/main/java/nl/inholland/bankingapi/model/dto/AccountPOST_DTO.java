package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.User;

public record AccountPOST_DTO(long userId, String IBAN, Double balance, Double absoluteLimit, AccountType accountType) {

}

