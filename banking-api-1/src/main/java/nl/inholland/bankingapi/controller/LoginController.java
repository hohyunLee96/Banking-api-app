package nl.inholland.bankingapi.controller;

import nl.inholland.bankingapi.model.AuthenticationResult;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.LoginRequest_DTO;
import nl.inholland.bankingapi.model.dto.LoginResponse_DTO;
import nl.inholland.bankingapi.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/login")
public class LoginController {
    private final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse_DTO> login(@RequestBody LoginRequest_DTO loginRequest) {
        // Validate the login credentials and authenticate the user
        AuthenticationResult authenticationResult = authenticationService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        if (authenticationResult.isAuthenticated()) {
            // User is authenticated, generate the access and refresh tokens
            String accessToken = authenticationService.generateAccessToken(authenticationResult.getUser());
            String refreshToken = authenticationService.generateRefreshToken(authenticationResult.getUser());

            // Create the response DTO with both tokens
            LoginResponse_DTO responseDTO = new LoginResponse_DTO(accessToken, refreshToken);

            // Return the response with a 200 OK status
            return ResponseEntity.ok(responseDTO);
        } else {
            // Authentication failed, return an error response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}



