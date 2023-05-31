package nl.inholland.bankingapi.service;

import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.RegisterRequestDTO;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void register(RegisterRequestDTO registerRequestDTO) {
        try {
            User user = new User(
                    registerRequestDTO.email(),
                    bCryptPasswordEncoder.encode(registerRequestDTO.password()),
                    registerRequestDTO.firstName(),
                    registerRequestDTO.lastName(),
                    registerRequestDTO.birthDate(),
                    registerRequestDTO.postalCode(),
                    registerRequestDTO.address(),
                    registerRequestDTO.city(),
                    registerRequestDTO.phoneNumber(),
                    registerRequestDTO.userType()
            );

            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Unable to register user.");
        }

    }

}
