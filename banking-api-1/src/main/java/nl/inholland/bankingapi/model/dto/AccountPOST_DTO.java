package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.AccountType;

public record AccountPOST_DTO(long userId, double absoluteLimit, AccountType accountType, boolean isActive) {

}

