package nl.inholland.bankingapi.controller;

import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.model.AuthenticationRequest;
import nl.inholland.bankingapi.model.AuthenticationResponse;
import nl.inholland.bankingapi.model.RegisterRequest;
import nl.inholland.bankingapi.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.login(registerRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

}
