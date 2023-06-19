package nl.inholland.bankingapi.service;

import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.LoginResponseDTO;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //retrieves user from repository based on the provided email
    //verifies the password, generates a JWT,
    //returns a LoginResponseDTO object with the JWT and user information
    public LoginResponseDTO login(String email, String password) throws javax.naming.AuthenticationException {

        User user = this.userRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new javax.naming.AuthenticationException("User not found"));
        if (!user.getPassword().isEmpty()) {
            //Check if the password hash matches the provided password
            if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                //Return a JWT to the client
                String jwt = jwtTokenProvider.createToken(user.getEmail(), user.getUserType());
                //creates a new LoginResponseDTO object and returns it
                return new LoginResponseDTO(jwt, user.getEmail(), user.getId());
            } else {
                throw new javax.naming.AuthenticationException("Incorrect email/password");
            }

        } else {
            throw new javax.naming.AuthenticationException("Password is empty");
        }

    }

}
