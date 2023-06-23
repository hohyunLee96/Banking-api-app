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
import nl.inholland.bankingapi.model.dto.AccountPUT_DTO;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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

    public
    JwtTokenProvider jwtTokenProvider;

    JwtTokenFilter jwtTokenFilter;

    HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
//        accountService = new AccountService(accountRepository, userRepository, jwtTokenProvider, jwtTokenFilter, request);
    }

    @Test
    void saveAccount() throws Exception {
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null);

        Account dummyAccount = new Account(user, "NL21INHO0123400081", 90000.00, 0.00, AccountType.CURRENT, true);
        AccountPOST_DTO dto = new AccountPOST_DTO(user.getId(), dummyAccount.getAbsoluteLimit(), dummyAccount.getAccountType(), dummyAccount.getIsActive());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.existsByUserIdAndAccountType(user.getId(), dummyAccount.getAccountType())).thenReturn(false);
        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(dummyAccount);
        Account createdAccount = accountService.addAccount(dto);
        assertEquals(dummyAccount, createdAccount);
    }

//    @Test
//    void employeeCannotOwnAccount() throws Exception {
//        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
//                "Ams", "+3148458y48", UserType.ROLE_EMPLOYEE, true, 100.0, 5200.00, null);
//
//        Account dummyAccount = new Account(user, "NL21INHO0123400081", 90000.00, 0.00, AccountType.CURRENT, true);
//        AccountPOST_DTO dto = new AccountPOST_DTO(user.getId(), dummyAccount.getBalance(), dummyAccount.getAbsoluteLimit(), dummyAccount.getAccountType(), dummyAccount.getIsActive());
//        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
//        when(accountRepository.existsByUserIdAndAccountType(user.getId(), dummyAccount.getAccountType())).thenReturn(false);
//        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
//        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(dummyAccount);
//
//        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
//            Account createdAccount = accountService.addAccount(dto);
//        });
//
//        assertEquals("Employee type cannot own accounts", exception.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//    }

    @Test
    void customerCannotOwnTwoAccountPerAccountType() throws Exception {
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null);

        Account dummyAccount = new Account(user, "NL21INHO0123400081", 90000.00, 0.00, AccountType.CURRENT, true);
        AccountPOST_DTO dto = new AccountPOST_DTO(user.getId(), dummyAccount.getAbsoluteLimit(), dummyAccount.getAccountType(), dummyAccount.getIsActive());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.existsByUserIdAndAccountType(user.getId(), dummyAccount.getAccountType())).thenReturn(true);
        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(dummyAccount);

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
            Account createdAccount = accountService.addAccount(dto);
        });

        assertEquals("User already has an account of type " + dummyAccount.getAccountType(), exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void modifyAbsoluteLimitOfAccount() throws Exception {
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null);

        Account dummyAccount = new Account(user, "NL21INHO0123400081", 90000.00, 10.00, AccountType.CURRENT, true);
        AccountPUT_DTO dto = new AccountPUT_DTO(8.0, dummyAccount.getIsActive());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(dummyAccount));
        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(dummyAccount);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.getAllAccountsByUserId(user.getId())).thenReturn(List.of(
                        new Account(user, "NL21INHO0123400081", 90000.00, 5.00, AccountType.CURRENT, true)
                )
        );
        when(accountRepository.existsByUserIdAndAccountType(user.getId(), dummyAccount.getAccountType())).thenReturn(false);
        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(dummyAccount);
        Account modifiedAccount = accountService.modifyAccount(1L, dto);
        assertEquals(8.0, modifiedAccount.getAbsoluteLimit());
    }
    @Test
    void returnErrorWhenAbsoluteLimitIsHigherThanBalance() throws Exception {
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null);

        Account dummyAccount = new Account(user, "NL21INHO0123400081", 0.0, 10.00, AccountType.CURRENT, true);
        AccountPUT_DTO dto = new AccountPUT_DTO(800000.0, dummyAccount.getIsActive());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(dummyAccount));
        when(accountRepository.existsByUserIdAndAccountType(user.getId(), dummyAccount.getAccountType())).thenReturn(false);
        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(dummyAccount);

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
            Account modifiedAccount = accountService.modifyAccount(1L, dto);
        });

        assertEquals("Absolute limit cannot be higher than account balance", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void deactivateAccount() throws Exception {
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null);

        Account dummyAccount = new Account(user, "NL21INHO0123400081", 90000.00, 10.00, AccountType.CURRENT, true);
        AccountPUT_DTO dto = new AccountPUT_DTO(dummyAccount.getAbsoluteLimit(), false);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(dummyAccount));
        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(dummyAccount);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.getAllAccountsByUserId(user.getId())).thenReturn(List.of(
                        new Account(user, "NL21INHO0123400081", 90000.00, 5.00, AccountType.CURRENT, true)
                )
        );
        when(accountRepository.existsByUserIdAndAccountType(user.getId(), dummyAccount.getAccountType())).thenReturn(false);
        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(dummyAccount);
        Account modifiedAccount = accountService.modifyAccount(1L, dto);
        assertEquals(false, modifiedAccount.getIsActive());
    }

    @Test
    public void testCreateIBAN() {
        String iban = accountService.createIBAN();

        // Assert that the generated IBAN has the correct format
        String ibanPattern = "NL\\d{2}INHO0\\d{9}";
        assertTrue(Pattern.matches(ibanPattern, iban));
    }

    @Test
    public void testGetAccountById_ExistingId_ReturnsAccountDTO() {
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null);
        Account dummyAccount = new Account(1L, new User(1L, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null), "NL21INHO0123400081", 90000.00, 10.00, AccountType.CURRENT, true);
        // Arrange
        long accountId = 1L;
        when(accountRepository.findById(dummyAccount.getAccountId())).thenReturn(Optional.of(dummyAccount));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        AccountGET_DTO get_dto = accountService.accountGETDto(dummyAccount);
        assertEquals(dummyAccount.getIBAN(), get_dto.IBAN());
        // Set up account object with test data
    }

    @Test
    public void testGetAccountById_NonExistingId_ReturnsAccountDTO() {
        // Create a dummy account with a non-existing ID
        long nonExistingId = 1L;
        when(accountRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Perform the operation and assert the expected exception
        assertThrows(EntityNotFoundException.class, () -> {
            accountService.getAccountById(nonExistingId);
        });
    }


//    @Test
//    void getAllAccounts_shouldReturnAccountDTOs() {
//        // Prepare test data
//        Integer offset = 0;
//        Integer limit = 10;
//        String firstName = "John";
//        String lastName = "Doe";
//        AccountType accountType = AccountType.SAVINGS;
//        Double absoluteLimit = 1000.0;
//        Boolean isActive = true;
//        Long user = 1L;
//
//        User loggedInUser = new User();
//        loggedInUser.setId(1L);
//        loggedInUser.setUserType(UserType.ROLE_CUSTOMER);
//
//        Account account1 = new Account();
//        account1.setAccountId(1L);
//        account1.setUser(loggedInUser);
//
//        Account account2 = new Account();
//        account2.setAccountId(2L);
//        account2.setUser(new User());
//
//        List<Account> accountList = Arrays.asList(account1, account2);
////        Page<Account> accountPage = new PageImpl<>(accountList);
////
////        when(accountRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(accountPage);
//        when(accountService.getLoggedInUser(request)).thenReturn(loggedInUser);
//
//        // Call the method under test
//        List<AccountGET_DTO> result = accountService.getAllAccounts(offset, limit, firstName, lastName, accountType, absoluteLimit, isActive, user);
//
//        // Assert the returned list of AccountGET_DTO
//        assertEquals(1, result.size());
//        assertEquals(account1.getAccountId(), result.get(0).accountId());
//    }


//    @Test
//    void getLoggedInUser_shouldReturnLoggedInUser() {
//        // Prepare test data
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        String receivedToken = "your_received_token";
//        // Mock the behavior of the jwtTokenFilter.getToken method
//        when(jwtTokenFilter.getToken(request)).thenReturn(receivedToken);
//
//        // Continue with the rest of your test code
//        // ...
//    }

}