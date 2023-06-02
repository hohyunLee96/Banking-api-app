package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.AccountType;
import org.apache.catalina.User;

public record AccountGET_DTO (long userId, String IBAN, double balance, double absoluteLimit, AccountType accountType, boolean isActive){

