package nl.inholland.bankingapi.model.dto;

public record UserGET_DTO(long userId, String email, String firstName, String lastName, String birthDate, String postalCode, String address, String city, String phoneNumber, String userType, Boolean hasAccount) {

}
