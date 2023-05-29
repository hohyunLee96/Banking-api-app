package nl.inholland.bankingapi.controller;

import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.model.dto.RegisterRequestDTO;
import nl.inholland.bankingapi.model.dto.ResponseTokenDTO;
import nl.inholland.bankingapi.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequestDTO registerRequestDTO) {
        authenticationService.register(registerRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ResponseTokenDTO> authenticate(
            @RequestBody ResponseTokenDTO responseTokenDTO) {
        return ResponseEntity.ok(authenticationService.authenticate(responseTokenDTO));
    }

}
