package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.filter.JwtTokenFilter;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.AccountGET_DTO;
import nl.inholland.bankingapi.model.dto.AccountIbanGET_DTO;
import nl.inholland.bankingapi.model.dto.AccountPOST_DTO;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.specifications.AccountSpecifications;
import nl.inholland.bankingapi.model.specifications.TransactionSpecifications;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

//import javax.persistence.EntityNotFoundException;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private List<Account> accounts;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenFilter jwtTokenFilter;

    private final HttpServletRequest request;


    public AccountService(AccountRepository accountRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider, JwtTokenFilter jwtTokenFilter, HttpServletRequest request) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenFilter = jwtTokenFilter;
        this.request = request;
    }

    private Account mapDtoToAccount(AccountPOST_DTO dto) {
        Account account = new Account();
        String iban = createIBAN();
        while(isIbanPresent(iban)){
            iban = createIBAN();
        }
        User user = userRepository.findUserById(dto.userId());
        account.setUser(user);
        if(!account.getUser().getHasAccount()){
            userHasAccount(user);
        }
        account.setIBAN(iban);
        account.setBalance(dto.balance());
        account.setAbsoluteLimit(dto.absoluteLimit());
        account.setAccountType(dto.accountType());
        account.setIsActive(true);
        return account;
    }
    public void userHasAccount(User user){
        user.setHasAccount(true);
        userRepository.save(user);
    }


    private Account mapDtoToAccountPut(long id,AccountGET_DTO dto) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Account not found"));
//        User user = userRepository.findUserBySpecificAccountId(id);
//        account.setUser(user);
//        if(!accountRepository.getAccountByUserId(id).isActive()) {
//            user.setHasAccount(false);
//        }
        account.setIBAN(account.getIBAN());
        account.setBalance(account.getBalance());
        account.setAbsoluteLimit(account.getAbsoluteLimit());
        account.setAccountType(account.getAccountType());
        account.setIsActive(false);
        return account;
    }
    private AccountGET_DTO accountGETDto(Account account){
        return new AccountGET_DTO(
                account.getAccountId(),
                account.getUser().getId(),
                account.getIBAN(),
                account.getBalance(),
                account.getAbsoluteLimit(),
                account.getAccountType(),
                account.getIsActive()
        );
    }
    private AccountIbanGET_DTO accountIbanGET_DTO(Account account){
        return new AccountIbanGET_DTO(
                account.getUser().getFirstName(),
                account.getUser().getLastName(),
                account.getIBAN()
        );
    }

    public List<AccountGET_DTO> getAllAccountsByUserId(long id) {
        List<AccountGET_DTO> accountsOwnedBySpecificUser = new ArrayList<>();
        for (Account account : accountRepository.getAllAccountsByUserId(id)) {
            accountsOwnedBySpecificUser.add(accountGETDto(account));
        }
        return accountsOwnedBySpecificUser;
    }
    public List<AccountGET_DTO> getAllAccounts(Integer offset, Integer limit, String firstName, String lastName, AccountType accountType, Double absoluteLimit, Boolean isActive, Long user) {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Account>accountSpecification = AccountSpecifications.getSpecifications(firstName, lastName, accountType, absoluteLimit, isActive, user);

        List<AccountGET_DTO> userAccounts = new ArrayList<>();
        List<AccountGET_DTO> accounts = new ArrayList<>();
        for (Account account : accountRepository.findAll(accountSpecification, pageable)) {
            accounts.add(accountGETDto(account));
            if(account.getUser().getId().equals(getLoggedInUser(request).getId())){
                Long userId = account.getUser().getId();
                Double totalBalance = accountRepository.getTotalBalanceByUserId(userId);
                userAccounts.add(accountGETDto(account));
            }
        }
        if (getLoggedInUser(request).getUserType().equals(UserType.ROLE_CUSTOMER)) {
            return userAccounts;
        }
        return accounts;
    }
    public User getLoggedInUser(HttpServletRequest request) {
        // Get JWT token and the information of the authenticated user
        String receivedToken = jwtTokenFilter.getToken(request);
        jwtTokenProvider.validateToken(receivedToken);
        Authentication authenticatedUserUsername = jwtTokenProvider.getAuthentication(receivedToken);
        String userEmail = authenticatedUserUsername.getName();
        return userRepository.findUserByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }

    public AccountGET_DTO getAccountById(long id ) {
        Account account =accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        return accountGETDto(account);
    }


    public List<AccountIbanGET_DTO> getIBANByUserFirstName(String firstName) {
        List<AccountIbanGET_DTO> accounts = new ArrayList<>();
        for (Account account : accountRepository.getIBANByUserFirstName(firstName)) {
            accounts.add(accountIbanGET_DTO(account));
        }
        return accounts;
    }

    public Account addAccount(AccountPOST_DTO account) {
        return accountRepository.save(this.mapDtoToAccount(account));
    }

    public AccountGET_DTO getAccountByUserId(long id) {
        return accountRepository.getAccountByUserId(id);
    }

    public Account disableAccount(@PathVariable long id, @RequestBody AccountGET_DTO accountGET_dto) {
        Account account = mapDtoToAccountPut(id, accountGET_dto);
        account.getUser().setHasAccount(isUserHasNoActiveAccounts(account.getUser().getId()));
        userRepository.save(account.getUser());
        return accountRepository.save(account);
    }
    public boolean isUserHasNoActiveAccounts(long id){
        List<Account> accounts = accountRepository.getAllAccountsByUserId(id);
        for (Account account : accounts) {
            if(account.getIsActive()){
                return false;
            }
        }
        return true;
    }

    public String createIBAN() {
        String firstLetters = "NL";
        Random random = new Random();

        String randomNumber = String.format("%02d", random.nextInt(100));

        String lastLetters = "INHO0";
        String randomNumber2 = String.format("%09d", random.nextInt(1000000000));

        String iban = firstLetters + randomNumber + lastLetters + randomNumber2;

        return iban;
    }

    public Double getTotalBalanceByUserId(long id) {
        return accountRepository.getTotalBalanceByUserId(id);
    }



    public Account getAccountByIBAN(String IBAN) {
        if(!isIbanPresent(IBAN)){
            throw new EntityNotFoundException("Iban not found " + IBAN);
        }
        return accountRepository.findAccountByIBAN(IBAN);
    }
    public boolean isIbanPresent (String iban){
        return (accountRepository.findAccountByIBAN(iban) != null);
    }
}
