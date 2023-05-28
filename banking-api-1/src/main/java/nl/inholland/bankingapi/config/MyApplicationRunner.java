package nl.inholland.bankingapi.config;

import jakarta.transaction.Transactional;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import nl.inholland.bankingapi.service.AccountService;
import nl.inholland.bankingapi.service.TransactionService;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class MyApplicationRunner implements ApplicationRunner {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final UserRepository userRepository;

    private final TransactionService transactionService;

    private final UserService userService;

    private User user1;


    private final AccountService accountService;

    public MyApplicationRunner(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository, TransactionService transactionService, UserService userService, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        user1 = new User("aizaz", "123", List.of(UserType.user));
        userService.addUser(user1);
        userRepository.save(user1);
    }
}
