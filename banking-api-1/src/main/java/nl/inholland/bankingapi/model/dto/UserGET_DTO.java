package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.UserType;

public record UserGET_DTO(long userId, String email, String password, String firstName, String lastName, String birthDate, String postalCode, String address, String city, String phoneNumber, String userType, Boolean hasAccount) {

}
