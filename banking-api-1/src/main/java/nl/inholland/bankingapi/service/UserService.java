package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.UserGET_DTO;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + id));
    }

    public User mapUserToDTO(UserGET_DTO userGET_dto) {

        User user = new User();
        user.setId(userGET_dto.userId());
        user.setFirstName(userGET_dto.firstName());
        user.setLastName(userGET_dto.lastName());
        user.setBirthDate(userGET_dto.birthDate());
        user.setAddress(userGET_dto.address());
        user.setPostalCode(userGET_dto.postalCode());
        user.setCity(userGET_dto.city());
        user.setPhoneNumber(userGET_dto.phoneNumber());
        user.setEmail(userGET_dto.email());
        user.setUserType(Arrays.asList(userGET_dto.userType()));
        user.setHasAccount(userGET_dto.hasAccount());

        return user;
    }

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

}
