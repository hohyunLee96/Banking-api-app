package nl.inholland.bankingapi.model.dto;

public record UserPOST_DTO(String email, String password, String firstName, String lastName, String birthDate, String postalCode, String address, String city, String phoneNumber, String userType, Boolean hasAccount) {
}
