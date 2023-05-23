package nl.inholland.bankingapi.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.model.AuthenticationRequest;
import nl.inholland.bankingapi.model.AuthenticationResponse;
import nl.inholland.bankingapi.model.RegisterRequest;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    //allows creating user, save it to database and then return generated token
    public AuthenticationResponse login(RegisterRequest registerRequest) {
//        var user = User.builder()
//                .username(registerRequest.getUsername())
//                .password(registerRequest.getPassword())
//                .userType(registerRequest.getUserType())
//                .build();
//        userRepository.save(user);
//        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
//                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        var user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));// Use UserDetailsService
        var jwtToken = jwtService.generateToken(user); // Pass userDetails instead of user

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}