package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.exception.ApiRequestException;
import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapi.filter.JwtTokenFilter;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.UserGET_DTO;
import nl.inholland.bankingapi.model.dto.UserPOST_DTO;
import nl.inholland.bankingapi.model.dto.UserPUT_DTO;
import nl.inholland.bankingapi.model.specifications.UserSpecifications;
import nl.inholland.bankingapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.util.List;

import static java.lang.Long.parseLong;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenFilter jwtTokenFilter;
    private final UserSpecifications userSpecifications;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ModelMapper modelMapper, JwtTokenProvider jwtTokenProvider, JwtTokenFilter jwtTokenFilter, UserSpecifications userSpecifications) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenFilter = jwtTokenFilter;
        this.userSpecifications = userSpecifications;

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

    public List<UserGET_DTO> getAllUsers(String firstName, String lastName, boolean hasAccount, String email, String userType, String postalCode, String city, String phoneNumber, String address, String birthDate, AccountType excludedAccountType) {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<User> specification = UserSpecifications.getSpecifications(firstName, lastName, hasAccount, email, userType, postalCode, city, phoneNumber, address, birthDate ,excludedAccountType);
        if (excludedAccountType != null) {
            specification = specification.and(UserSpecifications.hasNoAccountType(excludedAccountType));
        }
        List<UserGET_DTO> users = new ArrayList<>();
        for (User user : userRepository.findAll(specification, pageable)) {
            users.add(convertUserResponseToDTO(user));
        }
        return users;
    }
    public UserGET_DTO convertUserResponseToDTO(User user) {
        return new UserGET_DTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getPostalCode(),
                user.getAddress(),
                user.getCity(),
                user.getPhoneNumber(),
                user.getUserType(),
                user.getHasAccount()
        );
    }

    public User registerUser(UserPOST_DTO dto) {
        // Check if the user already exists
        if (userRepository.findUserByEmail(dto.email()).isPresent()) {
            throw new ApiRequestException("User with the same email address already exists", HttpStatus.CONFLICT);
        }
        try {
            isPasswordValid(dto.password(), dto.passwordConfirm());
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return userRepository.save(this.mapDtoToUser(dto));
    }

    public User updateUser(long id, UserPUT_DTO dto) {
        User userToUpdate = userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userRepository.save(modelMapper.map(userToUpdate, User.class));
    }

    //delete user of specific id
    public void deleteUserById(Long id) {
        //check if the user has an account
        if (userRepository.findById(id).get().getHasAccount()) {
            throw new ApiRequestException("User has an account", HttpStatus.CONFLICT);
        }
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

