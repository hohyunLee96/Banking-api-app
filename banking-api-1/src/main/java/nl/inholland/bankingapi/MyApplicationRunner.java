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

        User user1 = new User("user@email.com", bCryptPasswordEncoder.encode("1234")
                , "User", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.USER,100.0,1000.00);
        User user2 = new User("employee@email.com", bCryptPasswordEncoder.encode("1234"),
                "User", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.EMPLOYEE,100.0,1000.00);
        User customer= new User("customer@email.com", bCryptPasswordEncoder.encode("1234"),
                "User", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.CUSTOMER,100.0,1000.00);

        Account accountFrom = new Account(user1, "NL21INHO0123400081", 10000000.00, 0.00, AccountType.CURRENT);
        Account accountTo = new Account(user2, "NL21INHO0123400082", 10000000.00, 0.00, AccountType.CURRENT);

     Transaction transaction = new Transaction(accountFrom, accountTo, 100.00, LocalDateTime.now(), TransactionType.DEPOSIT, user2);
        transactionRepository.save(transaction);
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(customer);
    }

}