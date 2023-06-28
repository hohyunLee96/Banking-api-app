package nl.inholland.bankingapi.model.dto;

//used to serialize the response object into JSON or another appropriate format
public class LoginResponse_DTO {
    private String accessToken;
    private String refreshToken;

    public LoginResponse_DTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
