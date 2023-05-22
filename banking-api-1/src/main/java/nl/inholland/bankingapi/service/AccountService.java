package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.dto.AccountPOST_DTO;
import nl.inholland.bankingapi.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private List<Account> accounts;

    public AccountService(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Account> getAllAccounts(){
        return accountRepository.getAllAccounts();
    }

//    public Account createNewAccount(AccountPOST_DTO account){
//        return accountRepository.save(new Account(userRepository.findUserById(account.getUserId()), account.getIBAN(), account.getBalance()));
//    }
    public Account createNewAccount(AccountPOST_DTO account){
        return accountRepository.save(new Account(account.getIBAN(), account.getBalance(), account.getAbsoluteLimit(),account.getAccountType()));
    }
}
