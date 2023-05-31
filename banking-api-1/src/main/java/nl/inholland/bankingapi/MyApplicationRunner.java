package nl.inholland.bankingapi;

import lombok.RequiredArgsConstructor;
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
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MyApplicationRunner implements ApplicationRunner {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        loadInformationForDB();
    }

    public void loadInformationForDB() {

        User user1 = new User("user@email.com", "1234", "User1", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", List.of(UserType.ROLE_USER));
        User user2 = new User("employee@email.com", "1234", "User2", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", Arrays.asList(UserType.ROLE_EMPLOYEE, UserType.ROLE_USER));

        Account accountFrom = new Account(user1, "NL21INHO0123400081", 100.00, 100.00, AccountType.CURRENT);
        Account accountTo = new Account(user2, "NL21INHO0123400082", 10000000.00, 100.00, AccountType.CURRENT);

     Transaction transaction = new Transaction(accountFrom, accountTo, 100.00, LocalDateTime.now(), TransactionType.DEPOSIT, user2);
        transactionRepository.save(transaction);
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    public void LoadAccounts() {


    }
}