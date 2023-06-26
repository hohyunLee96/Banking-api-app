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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    UserRepository userRepository;
    @InjectMocks
    AccountService accountService;

    public
    JwtTokenProvider jwtTokenProvider;

    JwtTokenFilter jwtTokenFilter;

    Authentication authenticatedUser;

    HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mock dependencies
        JwtTokenFilter jwtTokenFilter = mock(JwtTokenFilter.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        UserRepository userRepository = mock(UserRepository.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Mock the behavior of JwtTokenFilter
        String receivedToken = "your_mocked_token";
        when(jwtTokenFilter.getToken(eq(request))).thenReturn(receivedToken);

        // Mock the behavior of JwtTokenProvider
        doNothing().when(jwtTokenProvider).validateToken(eq(receivedToken));
        Authentication authenticatedUser = mock(Authentication.class);
        when(authenticatedUser.getName()).thenReturn("employee@email.com");
        when(jwtTokenProvider.getAuthentication(eq(receivedToken))).thenReturn(authenticatedUser);
        User mockedUser = new User("employee@email.com", "1234", "User2", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.ROLE_EMPLOYEE, 500.00, 10000.00, true);
        when(userRepository.findUserByEmail(eq("employee@email.com"))).thenReturn(Optional.of(mockedUser));

        // Create the AccountService instance with the mock dependencies
        accountService = new AccountService(accountRepository, userRepository,jwtTokenProvider, jwtTokenFilter,request);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockedUser));
//        accountService = new AccountService(accountRepository, userRepository, jwtTokenProvider, jwtTokenFilter, request);
    }

    @Test
    void saveAccount() throws Exception {
        User user = new User(1L, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null);
        userRepository.save(user);

        Account dummyAccount = new Account(user, "NL21INHO0123400081", 90000.00, 0.00, AccountType.CURRENT, true);
        AccountPOST_DTO dto = new AccountPOST_DTO(user.getId(), dummyAccount.getAbsoluteLimit(), dummyAccount.getAccountType(), dummyAccount.getIsActive());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.existsByUserIdAndAccountType(user.getId(), dummyAccount.getAccountType())).thenReturn(false);
        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
        when(accountRepository.save(any(Account.class))).thenReturn(dummyAccount);
        Account createdAccount = accountService.addAccount(dto);
        assertEquals(dummyAccount, createdAccount);
    }

    @Test
    void customerCannotOwnTwoAccountPerAccountType() throws Exception {
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null);

        Account dummyAccount = new Account(user, "NL21INHO0123400081", 90000.00, 0.00, AccountType.CURRENT, true);
        AccountPOST_DTO dto = new AccountPOST_DTO(user.getId(), dummyAccount.getAbsoluteLimit(), dummyAccount.getAccountType(), dummyAccount.getIsActive());
        accountService.mapDtoToAccount(dto);
        when(accountRepository.existsByUserIdAndAccountType(user.getId(), dummyAccount.getAccountType())).thenReturn(true);
        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
        when(accountRepository.save(any(Account.class))).thenReturn(dummyAccount);

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
            Account createdAccount = accountService.addAccount(dto);
        });

        assertEquals("User already has an account of type " + dummyAccount.getAccountType(), exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

//    @Test
//    void getAllAccountsWithOutAccountTypeShouldReturnAllAccountsExceptBankAccount() {
//        // Mock the behavior of accountRepository.findAll
//        Pageable pageable = PageRequest.of(0, 2);
//        Specification<Account> specification = Specification.where(null);
//        Account account1 = new Account(1L, new User(), "NL21INHO0123400081", 90000.00, 0.00, AccountType.CURRENT, true);
//        Account account2 = new Account(2L, new User(), "NL21INHO0123400082", 90000.00, 0.00, AccountType.SAVINGS, true);
//        Page<Account> pageAccounts = new PageImpl<>(List.of(account1, account2));
//
//        when(accountRepository.findAll(specification, pageable)).thenReturn(pageAccounts.getContent());
//
//        // Call the method under test
//        List<AccountGET_DTO> accounts = accountService.getAllAccounts(0, 2, null, null, null, null, null, null);
//        when(accountService.getAllAccounts(0, 2, null, null, null, null, null, null)).thenReturn(accounts);
//        // Perform assertions
//        assertEquals(pageAccounts.getTotalElements(), accounts.size());
//        // Additional assertions as needed
//    }




    @Test
    void modifyAbsoluteLimitOfAccount() throws Exception {
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, true, 100.0, 5200.00, null);

        Account dummyAccount = new Account(user, "NL21INHO0123400081", 90000.00, 10.00, AccountType.CURRENT, true);
        AccountPUT_DTO dto = new AccountPUT_DTO(8.0, dummyAccount.getIsActive());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(dummyAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(dummyAccount);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.existsByUserIdAndAccountType(user.getId(), dummyAccount.getAccountType())).thenReturn(false);
        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
        when(accountRepository.save(any(Account.class))).thenReturn(dummyAccount);
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
        when(accountRepository.save(any(Account.class))).thenReturn(dummyAccount);

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
        when(accountRepository.save(any(Account.class))).thenReturn(dummyAccount);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.existsByUserIdAndAccountType(user.getId(), dummyAccount.getAccountType())).thenReturn(false);
        when(accountRepository.findAccountByIBAN(dummyAccount.getIBAN())).thenReturn(null);
        when(accountRepository.save(any(Account.class))).thenReturn(dummyAccount);
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