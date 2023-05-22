package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.Account;
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
}
