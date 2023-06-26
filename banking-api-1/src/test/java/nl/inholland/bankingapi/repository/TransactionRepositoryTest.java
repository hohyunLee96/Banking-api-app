package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.ApiTestConfiguration;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(ApiTestConfiguration.class)
class TransactionRepositoryTests {
    User customer = new User("customer@email.com", "1234", "Customer", "Customer", "11-11-2000",
            "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 50.00, 70.00, true);
    User employee = new User("emplloyee@email.com", "1234", "Customer", "Customer", "11-11-2000",
            "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 50.00, 70.00, true);
    Account account1 = new Account(customer, "NL21INHO0123400081", 500.00, 0.00, AccountType.CURRENT, true);
    Account account2 = new Account(employee, "NL21INHO0123400082", 9000.00, 0.00, AccountType.CURRENT, true);
    Transaction transaction = new Transaction(account1, account2, 100.00, LocalDateTime.now(), TransactionType.TRANSFER, customer);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;
    @InjectMocks
    private TransactionService transactionService;


    @BeforeEach
    public void setUp() {
        userRepository.save(customer);
        userRepository.save(employee);
        accountRepository.save(account1);
        accountRepository.save(account2);
        transactionRepository.save(transaction);
    }

    @Test
    void savingTransactionShouldReturnSavedAccount() {
        transactionRepository.save(transaction);
        Transaction savedTransaction = transactionRepository.save(transaction);
        assertNotNull(savedTransaction);
        assertEquals(transaction.getPerformingUser(), savedTransaction.getPerformingUser());
        assertEquals(transaction.getFromIban(), savedTransaction.getFromIban());
        assertEquals(transaction.getToIban(), savedTransaction.getToIban());
        assertEquals(transaction.getType(), savedTransaction.getType());
    }

    @Test
    void savingTransactionShouldReturnSavedAccount2() {
        userRepository.save(customer);
        userRepository.save(employee);
        accountRepository.save(account1);
        accountRepository.save(account2);
        transactionRepository.save(transaction);
        Transaction savedTransaction = transactionRepository.save(transaction);
        assertNotNull(savedTransaction);
        assertEquals(transaction.getPerformingUser(), savedTransaction.getPerformingUser());
        assertEquals(transaction.getFromIban(), savedTransaction.getFromIban());
        assertEquals(transaction.getToIban(), savedTransaction.getToIban());
        assertEquals(transaction.getType(), savedTransaction.getType());
    }


    @Test
    void findTransactionByIdShouldReturnTransaction() {
        transactionRepository.save(transaction);
        Transaction savedTransaction = transactionRepository.save(transaction);
        Transaction foundTransaction = transactionRepository.findById(savedTransaction.getId()).orElse(null);
        assertNotNull(foundTransaction);
        assertEquals(transaction.getPerformingUser(), foundTransaction.getPerformingUser());
        assertEquals(transaction.getFromIban(), foundTransaction.getFromIban());
        assertEquals(transaction.getToIban(), foundTransaction.getToIban());
        assertEquals(transaction.getType(), foundTransaction.getType());
    }

    @Test
    void findAllByPerformingUserAndTimestampBetween() {
        //initialize user for transactions and start and end time
        User user = new User();
        userRepository.save(user);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        //create transaction and save it
        transactionRepository.save(transaction);
        assertDoesNotThrow(() -> transactionRepository.findAllByPerformingUserAndTimestampBetween(user, start, end));
    }


}


