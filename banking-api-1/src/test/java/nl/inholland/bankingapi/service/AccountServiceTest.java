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
import nl.inholland.bankingapi.model.specifications.AccountSpecifications;
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

import java.util.ArrayList;
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
    }

    @Test
    void saveAccount() throws Exception {
        User user = new User(1L, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, 5200.00, 100.0, true);
        userRepository.save(user);
        user.setId(9012L);
        user.setHasAccount(true);
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
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, 5200.00, 100.0, true);
        user.setId(9012L);
        user.setHasAccount(true);
        Account dummyAccount = new Account(user, "NL21INHO0123400081", 90000.00, 0.00, AccountType.CURRENT, true);
        AccountPOST_DTO dto = new AccountPOST_DTO(user.getId(), dummyAccount.getAbsoluteLimit(), dummyAccount.getAccountType(), dummyAccount.getIsActive());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
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

    @Test
    void modifyAbsoluteLimitOfAccount() throws Exception {
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, 5200.00, 100.0, true);

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
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, 5200.00, 100.0, true);

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
                "Ams", "+3148458y48", UserType.ROLE_CUSTOMER, 5200.00, 100.0, true);

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
                "Ams", "+3148458448", UserType.ROLE_CUSTOMER, 5200.00, 100.0,true);
        Account dummyAccount = new Account(1L, new User(1L, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458448", UserType.ROLE_CUSTOMER, 5200.00, 100.0, true), "NL21INHO0123400081", 90000.00, 10.00, AccountType.CURRENT, true);
        // Arrange
        dummyAccount.setAccountId(1L);
        user.setId(1L);
        user.setHasAccount(true);
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

    @Test
    public void accountSpecificationTest() {
        User user = new User(1l, "customer@email.com", "Bjds", "ddnf", "Lee", "2023-10-26", "1023TX", "Osdrop",
                "Ams", "+3148458y48", UserType.ROLE_EMPLOYEE, 5200.00, 100.0, true);
        user.setId(1L);
        Account dummyAccount = new Account(1L, user, "NL21INHO0123400081", 90000.00, 10.00, AccountType.CURRENT, true);
        when(userRepository.findById(dummyAccount.getUser().getId())).thenReturn(Optional.of(user));
        accountRepository.save(dummyAccount);
        List<AccountGET_DTO> accounts = new ArrayList<>();
        Specification<Account> accountSpecification = AccountSpecifications.getSpecifications("ddnf", null, null, null, null, null);
        when(accountRepository.findAll(accountSpecification,PageRequest.of(0, 10))).thenReturn(List.of(dummyAccount));
        for (Account account : accountRepository.findAll(accountSpecification, PageRequest.of(0, 10))) {
            accounts.add(accountService.accountGETDto(account));
        }
        assertEquals(1, accounts.size());
    }

    @Test
    void getTotalBalanceByUserId() {
        when(accountRepository.getTotalBalanceByUserId(1L)).thenReturn(100.0);
        assertEquals(100.0, accountService.getTotalBalanceByUserId(1L));
    }
}