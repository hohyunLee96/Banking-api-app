package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.dto.AccountPOST_DTO;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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
        account.setUser(userRepository.getUserById(dto.userId()));
        account.setIBAN(dto.IBAN());
        account.setIBAN(iban);
        account.setBalance(dto.balance());
        account.setAbsoluteLimit(dto.absoluteLimit());
        account.setAccountType(dto.accountType());
        return account;
    }

    public List<Account> getAllAccounts() {
        return (List<Account>) accountRepository.findAll();
    }
    public List<Account> getIBANByUserFirstName(String firstName) {
        return (List<Account>) accountRepository.getIBANByUserFirstName(firstName);
    }

    public Account addAccount(AccountPOST_DTO account){
        return accountRepository.save(this.mapDtoToAccount(account));
    }

    public Account getAccountById(long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    public String createIBAN() {
        String firstLetters = "NL";
        Random random = new Random();

        String randomNumber = String.format("%02d",random.nextInt(100));

        String lastLetters = "INHO0";
        String randomNumber2 = String.format("%09d", random.nextInt(1000000000));

        String iban = firstLetters + randomNumber + lastLetters + randomNumber2;

        return iban;
    }
}
