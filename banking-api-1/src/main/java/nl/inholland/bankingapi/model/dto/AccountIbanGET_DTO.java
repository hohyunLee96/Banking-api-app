package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.AccountType;
import org.apache.catalina.User;

public record AccountIbanGET_DTO (Long userId, String firstName, String lastName, String IBAN){}

