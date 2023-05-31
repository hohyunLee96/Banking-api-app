package nl.inholland.bankingapi.controller;

import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.model.dto.LoginRequestDTO;
import nl.inholland.bankingapi.model.dto.RegisterRequestDTO;
import nl.inholland.bankingapi.service.AuthenticationService;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO registerRequestDTO) {

        authenticationService.register(registerRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO) throws AuthenticationException {
        return ResponseEntity.ok(authenticationService.login(loginRequestDTO.email(), loginRequestDTO.password()));
    }

}
