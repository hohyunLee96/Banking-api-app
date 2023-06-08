package nl.inholland.bankingapi.model.dto;

public record LoginResponseDTO(String jwt, String refreshToken, String email, long id) {

}
