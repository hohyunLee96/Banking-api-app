package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.AccountGET_DTO;
import nl.inholland.bankingapi.model.dto.AccountIbanGET_DTO;
import nl.inholland.bankingapi.model.dto.AccountPOST_DTO;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

//import javax.persistence.EntityNotFoundException;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private List<Account> accounts;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
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
//        account.setIBAN(dto.IBAN());
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


    public List<AccountGET_DTO> getAllAccounts() {
        List<AccountGET_DTO> accounts = new ArrayList<>();
        for (Account account : accountRepository.findAll()) {
            accounts.add(accountGETDto(account));
        }
        return accounts;
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

    public List<Account> getAllAccountsByUserId(long id) {
        return accountRepository.getAllAccountsByUserId(id);
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
