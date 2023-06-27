package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.filter.JwtTokenFilter;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.UserGET_DTO;
import nl.inholland.bankingapi.model.dto.UserPOST_DTO;
import nl.inholland.bankingapi.model.specifications.UserSpecifications;
import nl.inholland.bankingapi.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private JwtTokenFilter jwtTokenFilter;
    @InjectMocks
    private UserService userService;

    private User employee;
    private User customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, bCryptPasswordEncoder, jwtTokenProvider, jwtTokenFilter);

        employee = new User("employee@email.com", bCryptPasswordEncoder.encode("1234"), "FirstName", "LastName", "11-11-2000",
                "1234", "Street", "City", "123456789", UserType.ROLE_EMPLOYEE, 1000.00, 1000.00, false);
        employee.setId(1L);

        customer = new User("customer@email.com", bCryptPasswordEncoder.encode("1234"), "FirstName", "LastName", "11-11-2000",
                "1234", "Street", "City", "123456789", UserType.ROLE_CUSTOMER, 1000.00, 1000.00, true);
        customer.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        when(userRepository.findUserByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(userRepository.findUserByEmail(customer.getEmail())).thenReturn(Optional.of(customer));



        userRepository.save(employee);
        userRepository.save(customer);

    }
    @Test
    void newUserShouldNotBeNull() {
        Assertions.assertNotNull(new User());
    }

    @Test
    void getUserByNonExistentIdShouldThrowEntityNotFoundException() {
        Exception e = Assertions.assertThrows(EntityNotFoundException.class, () ->userService.getUserById(3L));
        Assert.assertEquals("User not found for id: 3", e.getMessage());
    }

    @Test
    void getUserByExistentIdShouldReturnUserOfGivenId(){
        UserGET_DTO user = userService.convertUserResponseToDTO(userService.getUserById(2L));
        Assert.assertEquals(user.userId(), 2L);
    }

    @Test
    void getAllUsersShouldReturnAListOfAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<User> specification = UserSpecifications.getSpecifications(null, null, null, null, null, null, null, null, null, null, null, null);
        List<UserGET_DTO> testUsers = new ArrayList<>();
        for (User user : userRepository.findAll(specification, pageable)) {
            testUsers.add(userService.convertUserResponseToDTO(user));
        }
        List<UserGET_DTO> expectedUsers = new ArrayList<>();
        expectedUsers.add(userService.convertUserResponseToDTO(employee));
        expectedUsers.add(userService.convertUserResponseToDTO(customer));
        Assert.assertEquals(expectedUsers.size(), testUsers.size());
        for (UserGET_DTO expectedUser : expectedUsers) {
            boolean found = false;
            for (UserGET_DTO actualUser : testUsers) {
                if (expectedUser.userId() == actualUser.userId()) {
                    // Compare other properties if needed
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        }
    }

    @Test
    void getAllUsersWhereUserTypeIsEmployeeShouldReturnAListOfAllUsers(){
        List<UserGET_DTO> users = userService.getAllUsers(null, null, null, null, null, null, null, null, null, null, UserType.ROLE_EMPLOYEE, null);
        List<UserGET_DTO> testUsers = new ArrayList<UserGET_DTO>();
        testUsers.add(userService.convertUserResponseToDTO(employee));
        Assert.assertEquals(users, testUsers);
    }

    @Test
    void registerUserWithEmailAlreadyInUserShouldThrowApiRequestException() {
        UserPOST_DTO testCustomer = new UserPOST_DTO("customer@email.com", "12341234$", "12341234$", "FirstName", "LastName", "2000-11-11",
                "1234", "Street", "City", "123456789", UserType.ROLE_CUSTOMER, true, 1000.00, 1000.00);
        Exception e = Assertions.assertThrows(ApiRequestException.class, () -> userService.registerUser(testCustomer));
        Assert.assertEquals("A user with the same email address already exists", e.getMessage());
    }

    @Test
    void registerUserWithPasswordTooShortShouldThrowApiRequestException() {
        UserPOST_DTO testCustomer = new UserPOST_DTO("test.customer@email.com", "1111", "1111", "FirstName", "LastName", "2000-11-11",
                "1234", "Street", "City", "123456789", UserType.ROLE_CUSTOMER, true, 1000.00, 1000.00);
        Exception e = Assertions.assertThrows(ApiRequestException.class, () -> userService.registerUser(testCustomer));
        Assert.assertEquals("Password must be at least 8 characters long", e.getMessage());
    }

    @Test
    void registerUserWithInvalidPasswordShouldThrowApiRequestException() {
        UserPOST_DTO testCustomer = new UserPOST_DTO("test.customer@email.com", "11111111", "11111111", "FirstName", "LastName", "2000-11-11",
                "1234", "Street", "City", "123456789", UserType.ROLE_CUSTOMER, true, 1000.00, 1000.00);
        Exception e = Assertions.assertThrows(ApiRequestException.class, () -> userService.registerUser(testCustomer));
        Assert.assertEquals("Password must contain at least one number and one special character", e.getMessage());
    }

    @Test
    void registerUserWithPasswordAndConfirmPasswordNotMatchingShouldThrowIllegalArgumentException() {
        UserPOST_DTO testCustomer = new UserPOST_DTO("test.customer@email.com", "12341234#", "56785678#", "FirstName", "LastName", "2000-11-11",
                "1234", "Street", "City", "123456789", UserType.ROLE_CUSTOMER, true, 1000.00, 1000.00);
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testCustomer));
        Assert.assertEquals("Passwords do not match", e.getMessage());
    }

    @Test
    void registerUserWithFirstNameContainingIllegalCharactersShouldThrowIllegalArgumentException() {
        UserPOST_DTO testCustomer = new UserPOST_DTO("test.customer@email.com", "12341234#", "12341234#", "#1.", "LastName", "2000-11-11",
                "1234", "Street", "City", "123456789", UserType.ROLE_CUSTOMER, true, 1000.00, 1000.00);
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testCustomer));
        Assert.assertEquals("Last name cannot contain any special characters or numbers", e.getMessage());
    }

    @Test
    void registerUserWithLastNameContainingIllegalCharactersShouldThrowIllegalArgumentException() {
        UserPOST_DTO testCustomer = new UserPOST_DTO("test.customer@email.com", "12341234#", "12341234#", "FirstName", "#1.", "2000-11-11",
                "1234", "Street", "City", "123456789", UserType.ROLE_CUSTOMER, true, 1000.00, 1000.00);
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testCustomer));
        Assert.assertEquals("Last name cannot contain any special characters or numbers", e.getMessage());
    }

    @Test
    void registerUserWithAgeBelow18IllegalCharactersShouldThrowIllegalArgumentException() {
        UserPOST_DTO testCustomer = new UserPOST_DTO("test.customer@email.com", "12341234#", "12341234#", "FirstName", "LastName", LocalDate.now().toString(),
                "1234", "Street", "City", "123456789", UserType.ROLE_CUSTOMER, true, 1000.00, 1000.00);
        Exception e = Assertions.assertThrows(ApiRequestException.class, () -> userService.registerUser(testCustomer));
        Assert.assertEquals("User must be at least 18 years old", e.getMessage());
    }

    @Test
    void registerUserWithAgeOver150IllegalCharactersShouldThrowIllegalArgumentException() {
        UserPOST_DTO testCustomer = new UserPOST_DTO("test.customer@email.com", "12341234#", "12341234#", "FirstName", "LastName", "1870-11-11",
                "1234", "Street", "City", "123456789", UserType.ROLE_CUSTOMER, true, 1000.00, 1000.00);
        Exception e = Assertions.assertThrows(ApiRequestException.class, () -> userService.registerUser(testCustomer));
        Assert.assertEquals("Birthdate cannot be further than 150 years ago", e.getMessage());
    }

    @Test
    void registerUserWithBirthDateInTheFutureIllegalCharactersShouldThrowIllegalArgumentException() {
        UserPOST_DTO testCustomer = new UserPOST_DTO("test.customer@email.com", "12341234#", "12341234#", "FirstName", "LastName", LocalDate.now().plusDays(1).toString(),
                "1234", "Street", "City", "123456789", UserType.ROLE_CUSTOMER, true, 1000.00, 1000.00);
        Exception e = Assertions.assertThrows(ApiRequestException.class, () -> userService.registerUser(testCustomer));
        Assert.assertEquals("Birthdate cannot be in the future", e.getMessage());
    }

    @Test
    void newlyRegisteredUserHasDefaultDailyLimit() {
        Assert.assertEquals(Optional.of(100.0), customer.getDailyLimit());
    }

    @Test
    void newlyRegisteredUserHasDefaultTransactionLimit() {
        Assert.assertEquals(Optional.of(500.0), customer.getTransactionLimit());
    }

    @Test
    public void deleteUserWithAnAccountShouldThrowApiRequestException() {
        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> userService.deleteUserById(2L));
        Assertions.assertEquals("Cannot delete user with an active account", exception.getMessage());
    }

    @Test
    public void deleteUserWithoutAnAccountShouldDeleteSuccessfully() {
        Assertions.assertDoesNotThrow(() -> userService.deleteUserById(1L));
    }
}
