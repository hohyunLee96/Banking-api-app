package nl.inholland.bankingapi;

import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import nl.inholland.bankingapi.service.AccountService;
import nl.inholland.bankingapi.service.TransactionService;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final UserService userService;
    private final AccountService accountService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MyApplicationRunner(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository, TransactionService transactionService, UserService userService, AccountService accountService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.userService = userService;
        this.accountService = accountService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        loadInformationForDB();
    }

    public void loadInformationForDB() {
        //Load users
        User user1 = new User("user@email.com", bCryptPasswordEncoder.encode("1234"), "User1", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", List.of(UserType.ROLE_USER), 1000.00, 1000.00,true);
        User user2 = new User("employee@email.com", bCryptPasswordEncoder.encode("1234"), "User2", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", List.of(UserType.ROLE_EMPLOYEE) ,10000.00, 10000.00, true);

        User customer = new User("customer@email.com", bCryptPasswordEncoder.encode("1234"), "Customer", "Customer", "11-11-2000",
                "123456789", "Street", "1234AB", "City", List.of(UserType.ROLE_CUSTOMER), 10000.00, 10000.00, true);

        //Load Accounts
        Account accountFrom = new Account(user1, "NL21INHO0123400081", 90000.00, 0.00, AccountType.CURRENT, true);
        Account accountTo = new Account(user2, "NL21INHO0123400082", 9000.00, 0.00, AccountType.SAVINGS, true);
        Account bank= new Account(user1, "NL01INHO0000000001", 9000.00, 0.00, AccountType.CURRENT, true);

         Transaction transaction = new Transaction(accountFrom, accountTo, 100.00, LocalDateTime.now(), TransactionType.DEPOSIT, user2);
            transactionRepository.save(transaction);
            accountRepository.save(accountFrom);
            accountRepository.save(accountTo);
            accountRepository.save(bank);
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(customer);
        }

    public void LoadAccounts() {


    }
}