package nl.inholland.bankingapi.service;

//import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.repository.UserRepository;
//import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String email, String password) {
        User user = userRepository.findUsersByEmail(email)
                .orElseThrow();
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public List<User> getAllUsers(){
        return (List<User>) userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
//    public User addUser(User user) {
//        if (userRepository.findUsersByEmail(user.getEmail()).isEmpty()) {
//            return userRepository.save(user);
//        }
//        throw new IllegalArgumentException("Email is already taken");
//    }
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    private final JwtTokenProvider jwtTokenProvider;
//
//    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider) {
//        this.userRepository = userRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    public User mapUserToDTO(UserGET_DTO userGET_dto){
//
//            User user = new User();
//            user.setId(userGET_dto.userId());
//            user.setFirstName(userGET_dto.firstName());
//            user.setLastName(userGET_dto.lastName());
//            user.setBirthDate(userGET_dto.birthDate());
//            user.setAddress(userGET_dto.address());
//            user.setPostalCode(userGET_dto.postalCode());
//            user.setCity(userGET_dto.city());
//            user.setPhoneNumber(userGET_dto.phoneNumber());
//            user.setEmail(userGET_dto.email());
//            user.setUserType(userGET_dto.userType());
//            user.setHasAccount(userGET_dto.hasAccount());
//
//            return user;
//    }
//
//    public List<User> getAllUsers(){
//        return (List<User>) userRepository.findAll();
//    }
//
//    public String login(String email, String password) throws Exception {
//        // See if a user with the provided username exists or throw exception
//        User user = this.userRepository
//                .findUsersByEmail(email)
//                .orElseThrow(() -> new AuthenticationException("User not found"));
//
//        // Check if the password hash matches
//        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
//            // Return a JWT to the client
//            return jwtTokenProvider.createToken(user.getEmail(), user.getUserType());
//        } else {
//            throw new AuthenticationException("Invalid email/password");
//        }
//    }
}