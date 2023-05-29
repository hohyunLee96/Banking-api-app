package nl.inholland.bankingapi.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.RegisterRequestDTO;
import nl.inholland.bankingapi.model.dto.ResponseTokenDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    public ResponseTokenDTO register(RegisterRequestDTO registerRequestDTO) {
        return null;
    }

    public ResponseTokenDTO authenticate(ResponseTokenDTO responseTokenDTO) {
        return null;
    }
}
