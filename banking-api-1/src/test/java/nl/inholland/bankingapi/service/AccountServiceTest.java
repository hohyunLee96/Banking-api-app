package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.filter.JwtTokenFilter;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.AccountGET_DTO;
import nl.inholland.bankingapi.model.dto.AccountPOST_DTO;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

//    @Mock
//    AccountService accountService;


    @Mock
    UserRepository userRepository;
    @InjectMocks
    AccountService accountService;

    //    @Autowired
//    public Boolean isCustomer(Long userId) {
//        return true;
//    }
    public
    JwtTokenProvider jwtTokenProvider;

    JwtTokenFilter jwtTokenFilter;

    HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
//        accountService = new AccountService(accountRepository, userRepository, jwtTokenProvider, jwtTokenFilter, request);
    }

//    protected Account mapDtoToAccount(AccountPOST_DTO dto) {
//        Account account = new Account();
//        String iban ="NL01INHO0000000031";
//        User user = new User("customer@email.com", "1234", "Customer", "Customer", "11-11-2000",
//                "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 10000.00, 10000.00, false);
//        account.setUser(user);
//        account.setIBAN(iban);
//        account.setBalance(dto.balance());
//        account.setAbsoluteLimit(dto.absoluteLimit());
//        account.setAccountType(dto.accountType());
//        account.setIsActive(true);
//        return account;
//    }
//    @Test
//    void saveAccount() {
//        AccountPOST_DTO accountDTO = new AccountPOST_DTO(1, "NL01INHO0000000001", 0.0, 1000.00, AccountType.SAVINGS, true);
//        Account account = new Account();
//
//        User user = new User("customer@email.com", "1234", "Customer", "Customer", "11-11-2000",
//                "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 10000.00, 10000.00, true);
//
//        // Create a mock UserRepository instance
//        UserRepository userRepository = mock(UserRepository.class);
//
//        // Set up the mock to return the user when findById is called with the provided userId
//        when(userRepository.findById(eq(accountDTO.userId()))).thenReturn(Optional.of(user));
//
//        // Inject the userRepository mock into your accountService instance
////        accountService.setUserRepository(userRepository);
//
//        when(accountRepository.save(account)).thenReturn(account);
//
//        Account createdAccount = accountService.addAccount(accountDTO);
//
//        assertEquals(account.getIBAN(), createdAccount.getIBAN());
//    }

    @Test
    void saveAccount() throws Exception{
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null);

        Account dummyAccount = new Account(user, "NL21INHO0123400081", 90000.00, 0.00, AccountType.CURRENT, true);
        AccountPOST_DTO dto = new AccountPOST_DTO(user.getId(), dummyAccount.getBalance(), dummyAccount.getAbsoluteLimit(), dummyAccount.getAccountType(), dummyAccount.getIsActive());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.existsByUserIdAndAccountType(user.getId(),dummyAccount.getAccountType())).thenReturn(false);
        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(dummyAccount);
        Account createdAccount= accountService.addAccount(dto);
        assertEquals(dummyAccount, createdAccount);
    }


    //    @Test
//    void getAllAccounts() {
//        // Create a list to hold the mock account data
//        List<Account> mockAccounts = Arrays.asList(
//                new Account(new User(),"NL01INHO0000000001", 1000.00, 0.0, AccountType.SAVINGS, true),
//                new Account(new User(),"NL01INHO0000000002", 1000.00, 0.0, AccountType.SAVINGS, true),
//                new Account(new User(),"NL01INHO0000000003", 1000.00, 0.0, AccountType.SAVINGS, true)
//        );
//
//        // Mock the account repository
//        AccountRepository accountRepository = mock(AccountRepository.class);
//        when(accountRepository.findAll()).thenReturn(mockAccounts);
//
//        accountService = new AccountService(accountRepository, userRepository, jwtTokenProvider, jwtTokenFilter, request);
//        // Create the object under test and invoke the method
//        List<AccountGET_DTO> result = accountService.getAllAccounts(null,null,null,null,null,null,null,null);
//
//        // Perform assertions on the result
//        assertEquals(mockAccounts.size(), result.size());
//        // Add additional assertions as needed for each account in the result list
//        // For example: assertEquals(mockAccounts.get(0), result.get(0));
//        // Make sure to override the equals() and hashCode() methods in the AccountGET_DTO class for accurate assertions
//    }
//    @Test
//    @WithMockUser(username = "employee@email.com", password = "1234", roles = "EMPLOYEE")
//    void getAllAccounts() {
//        // Create a list to hold the mock account data
//        List<Account> mockAccounts = Arrays.asList(
//                new Account(new User(), "NL01INHO0000000001", 1000.00, 0.0, AccountType.SAVINGS, true),
//                new Account(new User(), "NL01INHO0000000002", 1000.00, 0.0, AccountType.SAVINGS, true),
//                new Account(new User(), "NL01INHO0000000003", 1000.00, 0.0, AccountType.SAVINGS, true)
//        );
//
//        // Mock the dependencies
//        AccountRepository accountRepository = mock(AccountRepository.class);
//        UserRepository userRepository = mock(UserRepository.class);
//        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
//        JwtTokenFilter jwtTokenFilter = mock(JwtTokenFilter.class);
//        HttpServletRequest request = mock(HttpServletRequest.class);
//
//        // Configure the mock dependencies
//        when(accountRepository.findAll()).thenReturn(mockAccounts);
//        when(jwtTokenFilter.getToken(request)).thenReturn("mockToken");
//        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(new User("employee@email.com", "1234", "User2", "User", "11-11-2000",
//                "123456789", "Street", "1234AB", "City", UserType.ROLE_EMPLOYEE, 10000.00, 10000.00, true)));
//
//        // Create the object under test and invoke the method
//        AccountService accountService = new AccountService(accountRepository, userRepository, jwtTokenProvider, jwtTokenFilter, request);
//        List<AccountGET_DTO> result = accountService.getAllAccounts(null, null, null, null, null, null, null, null);
//
//        // Perform assertions on the result
//        assertEquals(mockAccounts.size(), result.size());
//        // Add additional assertions as needed for each account in the result list
//        // For example: assertEquals(mockAccounts.get(0), result.get(0));
//        // Make sure to override the equals() and hashCode() methods in the AccountGET_DTO class for accurate assertions
//    }


    //    @Test
//    void shouldReturnAccount(){
//        AccountPOST_DTO accountPOST_dto = new AccountPOST_DTO(1, "NL01INHO0000000001", 0.0, 1000.00, AccountType.CURRENT, true);
//        when(accountRepository.save(accountPOST_dto)).thenReturn(accountPOST_dto);
//
//        Account account = accountRepository.save(accountPOST_dto);
//
//    }
//    @Test
//    public void testAddAccount() {
//        AccountPOST_DTO accountDTO = new AccountPOST_DTO(1, "NL01INHO0000000001", 0.0, 1000.00, AccountType.CURRENT, true);
//
//        User user = new User();
//        user.setUserType(UserType.ROLE_CUSTOMER);
//        when(userRepository.findUserById(accountDTO.userId())).thenReturn(user);
//        when(accountRepository.existsByUserIdAndAccountType(accountDTO.userId(), accountDTO.accountType()))
//                .thenReturn(false);
//
//        accountService.addAccount(accountDTO);
//
////        verify(accountRepository, times(1)).save(any(Account.class));
//    }

    //    @Test
//    public void testAddAccount_employeeTypeThrowsException() {
//        AccountPOST_DTO accountDTO = new AccountPOST_DTO(1, "NL01INHO0000000001", 0.0, 1000.00, AccountType.SAVINGS, true);
//
//        User user = new User();
//        user.setUserType(UserType.ROLE_EMPLOYEE);
//        when(userRepository.findUserById(accountDTO.userId())).thenReturn(user);
//
//        Account account = new Account();
//        account.setUser(user);
//        account.setIBAN(accountDTO.IBAN());
//        account.setBalance(accountDTO.balance());
//        account.setAbsoluteLimit(accountDTO.absoluteLimit());
//        account.setAccountType(accountDTO.accountType());
//        account.setIsActive(accountDTO.isActive());
//
//        try {
//            accountRepository.save(account);
//        } catch (ApiRequestException ex) {
//            assertEquals("Employee type cannot own accounts", ex.getMessage());
//            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
//        }
//    }
//    @Test
//    public void testAddAccount_employeeTypeThrowsException() {
//        // Arrange
//        AccountPOST_DTO accountDTO = new AccountPOST_DTO(1, "NL01INHO0000000001", 0.0, 1000.00, AccountType.SAVINGS, true);
//
//        User user = new User();
//        user.setId(1L);
//        user.setUserType(UserType.ROLE_EMPLOYEE);
//        when(userRepository.findUserById(accountDTO.userId())).thenReturn(user);
//
//        // Act and Assert
//        ApiRequestException exception = Assertions.assertThrows(ApiRequestException.class, () -> accountService.addAccount(accountDTO));
//        assertEquals("Employee type cannot own accounts", exception.getMessage());
//    }
//    @Test
//    public void testAddAccount_WhenUserIsEmployee_ShouldThrowException() {
//        // Arrange
//        Long userId = 1L;
//        AccountType accountType = AccountType.SAVINGS;
//        double balance = 100.0;
//        double absoluteLimit = 1000.0;
//
//        AccountPOST_DTO accountDto = new AccountPOST_DTO();
//        accountDto.setUserId(userId);
//        accountDto.setAccountType(accountType);
//        accountDto.setBalance(balance);
//        accountDto.setAbsoluteLimit(absoluteLimit);
//
//        User user = new User();
//        user.setId(userId);
//        user.setUserType(UserType.ROLE_EMPLOYEE);
//        when(userRepository.findUserById(userId)).thenReturn(user);
//
//        // Act & Assert
//        Assertions.assertThrows(ApiRequestException.class, () -> accountService.addAccount(accountDto));
//
//        verifyZeroInteractions(accountRepository);
//    }

    @Test
    public void testAddAccount_WhenUserIsEmployee_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        AccountType accountType = AccountType.SAVINGS;
        double balance = 100.0;
        double absoluteLimit = 1000.0;

        AccountPOST_DTO accountDto = mock(AccountPOST_DTO.class);
        User user = new User();
        user.setUserType(UserType.ROLE_EMPLOYEE);

        when(accountDto.userId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Assertions.assertThrows(ApiRequestException.class, () -> accountService.addAccount(accountDto));

        verifyZeroInteractions(accountRepository);
    }


    @Test
    void userHasAccount() {
    }

    @Test
    void disableAccount() {
    }

    @Test
    void getAllAccountsByUserId() {
    }

//    @Test
//    void getAllAccounts() {
//        when(accountRepository.findAll()).thenReturn(
//                Stream.of(
//                        new AccountGET_DTO(2L, 1L, "NL01INHO0000000001", 1000.00, 0.0, AccountType.SAVINGS, true),
//                        new AccountGET_DTO(2L, 2L, "NL01INHO0000000002", 1000.00, 0.0,AccountType.SAVINGS, true),
//                        new AccountGET_DTO(3L, 3L, "NL01INHO0000000003", 1000.00, 0.0,AccountType.SAVINGS, true)).collect(Collectors.toList())));
//                )
//        );
//    }

    @Test
    void getLoggedInUser() {
    }

    @Test
    void getAccountById() {
    }

    @Test
    void getIBANByUserFirstName() {
    }

    @Test
    void addAccount() {
    }

    @Test
    void getAccountByUserId() {
    }

    @Test
    void isUserHasNoActiveAccounts() {
    }

    @Test
    void createIBAN() {
    }

    @Test
    void getTotalBalanceByUserId() {
    }

    @Test
    void getAccountByIBAN() {
    }

    @Test
    void isIbanPresent() {
    }


//    @Test
//    void testMapDtoToAccount() {
//        UserRepository userRepository = mock(UserRepository.class);
//        AccountService accountService = new AccountService();
//
//        // Create a dummy User object
//        User user = new User("user@example.com", "password", "John", "Doe", ...); // Create a valid User object here
//
//        // Mock the userRepository.findUserById() method to return the dummy User object
//        when(userRepository.findUserById(anyInt())).thenReturn(Optional.of(user));
//
//        // Call the mapDtoToAccount method
//        Account actualAccount = accountService.mapDtoToAccount(accountDTO, userRepository);
//
//        // Perform assertions on the actualAccount object
//        assertEquals(accountDTO.IBAN(), actualAccount.getIBAN());
//        assertEquals(accountDTO.balance(), actualAccount.getBalance());
//        assertEquals(accountDTO.absoluteLimit(), actualAccount.getAbsoluteLimit());
//        assertEquals(accountDTO.accountType(), actualAccount.getAccountType());
//        assertTrue(actualAccount.getIsActive());
//        assertEquals(user, actualAccount.getUser());
//    }


    @Test
    public void testGetAccountById_ExistingAccount() {
        // Arrange
        Long accountId = 1L;
        Account account = new Account(new User(), "NL01INHO0000000001", 0.0, 1000.00, AccountType.SAVINGS, true);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act
//        AccountGET_DTO result = accountService.getAccountById(accountId);

        // Assert
//        assertEquals(accountId, result.accountId());
        // Add additional assertions for other properties of the DTO if needed
    }

    @Test
    public void testGetAccountById_NonExistingAccount() {
        // Arrange
        long nonExistingAccountId = 100L;

        when(accountRepository.findById(nonExistingAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            accountService.getAccountById(nonExistingAccountId);
        });
    }
}