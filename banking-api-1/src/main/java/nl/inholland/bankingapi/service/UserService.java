package nl.inholland.bankingapi.service;

import jakarta.servlet.http.HttpServletRequest;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.filter.JwtTokenFilter;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.UserGET_DTO;
import nl.inholland.bankingapi.model.dto.UserPOST_DTO;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

import java.util.List;

import static java.lang.Long.parseLong;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenFilter jwtTokenFilter;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider, JwtTokenFilter jwtTokenFilter) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + id));
    }
    public List<User>getUsersWithoutAccount(Boolean hasAccount){
        return userRepository.findAllByHasAccount(hasAccount);
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
        user.setUserType(userGET_dto.userType());
        user.setHasAccount(userGET_dto.hasAccount());

        return user;
    }

    public User mapDtoToUser(UserPOST_DTO dto) {
        User user = new User();
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setBirthDate(dto.birthDate());
        user.setAddress(dto.address());
        user.setPostalCode(dto.postalCode());
        user.setCity(dto.city());
        user.setPhoneNumber(dto.phoneNumber());
        user.setEmail(dto.email());
        user.setUserType(dto.userType());
        user.setHasAccount(false);
        user.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        return user;
    }

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    public String login(String email, String password) throws javax.naming.AuthenticationException {
        // See if a user with the provided username exists or throw exception
        User user = this.userRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new javax.naming.AuthenticationException("User not found"));
        //Check if the password hash matches
        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            //Return a JWT to the client
            return jwtTokenProvider.createToken(user.getEmail(), user.getUserType());
        } else {
            throw new javax.naming.AuthenticationException("Incorrect email/password");
        }
    }

    public User registerUser(UserPOST_DTO dto) {
        // Check if the user already exists
        if (userRepository.findUserByEmail(dto.email()).isPresent()) {
            throw new ApiRequestException("User with the same email address already exists", HttpStatus.CONFLICT);
        }
        try {
            isPasswordValid(dto.password(), dto.passwordConfirm());
        } catch (IllegalArgumentException e) {
            throw e;
        }
        return userRepository.save(this.mapDtoToUser(dto));
    }

    public User updateUser(long id, UserPOST_DTO dto) {
        //check if the password is valid
        try {
            isPasswordValid(dto.password(), dto.passwordConfirm());
        } catch (IllegalArgumentException e) {
            throw e;
        }
        User userToUpdate = userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guitar not found"));
        userToUpdate.setFirstName(dto.firstName());
        userToUpdate.setLastName(dto.lastName());
        userToUpdate.setBirthDate(dto.birthDate());
        userToUpdate.setAddress(dto.address());
        userToUpdate.setPostalCode(dto.postalCode());
        userToUpdate.setCity(dto.city());
        userToUpdate.setPhoneNumber(dto.phoneNumber());
        userToUpdate.setEmail(dto.email());
        userToUpdate.setUserType(dto.userType());
        userToUpdate.setHasAccount(dto.hasAccount());
        userToUpdate.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        return userRepository.save(userToUpdate);
    }

    //delete user of specific id
    public void deleteUserById(Long id) {
        userRepository.delete(userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found.")));
    }

    private void isPasswordValid(String password, String passwordConfirm) throws IllegalArgumentException {

        // Check if the password is the same as the password confirmation
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if the password is long enough
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        // Check if the password contains at least one number and one special character
        if (!password.matches(".*\\d.*") || !password.matches(".*[!@#$%^&*].*")) {
            throw new IllegalArgumentException("Password must contain at least one number and one special character");
        }
    }


}

