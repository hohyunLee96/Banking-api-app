package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.dto.AccountPOST_DTO;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private List<Account> accounts;

//    public AccountService(List<Account> accounts) {
//        this.accounts = accounts;
//    }

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }
    private Account mapDtoToAccount(AccountPOST_DTO dto) {
        Account account = new Account();
        account.setUser(userRepository.findUserById(dto.user().getId()));
        account.setIBAN(dto.IBAN());
        account.setBalance(dto.balance());
        account.setAbsoluteLimit(dto.absoluteLimit());
        account.setAccountType(dto.accountType());
        return account;
    }
//    public List<Account> getAllAccounts(){
//        return (List<Account>)accountRepository.getAllAccounts();
//    }

    public List<Account> getAllAccounts() {
        return (List<Account>) accountRepository.findAll();
    }


    //    public Account createNewAccount(AccountPOST_DTO account){
//        return accountRepository.save(new Account(userRepository.findUserById(account.getUserId()), account.getIBAN(), account.getBalance()));
//    }
//    public Account createNewAccount(AccountPOST_DTO account){
//        return accountRepository.save(new AccountPOST_DTO(userRepository.findUserById(account.getUser().getId()),account.getIBAN(), account.getBalance(), account.getAbsoluteLimit(),account.getAccountType()));
//    }
    public Account addAccount(AccountPOST_DTO account){
        return accountRepository.save(this.mapDtoToAccount(account));
    }
}
