package nl.inholland.bankingapi.repository;

import nl.inholland.bankingapi.ApiTestConfiguration;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.AccountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//making isolated for each tests
@Import(ApiTestConfiguration.class)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User customer = new User("customer@email.com", "1234", "Customer", "Customer", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 5000.00, 7000.00, true);

        User customer2 = new User("customer@email.com", "1234", "Customer", "Customer", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 5000.00, 7000.00, true);
        User savedCustomer = userRepository.save(customer);
        accountRepository.saveAll(List.of(
                new Account(savedCustomer, "NL21INHO0123400081", 500.00, 0.00, AccountType.CURRENT, true),
                new Account(savedCustomer, "NL21INHO0123400082", 500.00, 0.00, AccountType.SAVINGS, true)
        ));
    }

    @Test
    void findAllShouldReturnAllAccounts() {
        List<Account> allAccounts = (List<Account>) accountRepository.findAll();
        assertNotNull(allAccounts);
        assertEquals(2, allAccounts.size());
    }

    @Test
    void findAccountByIBAN() {
        Account account = accountRepository.findAccountByIBAN("NL21INHO0123400081");
        assertEquals("NL21INHO0123400081", account.getIBAN());
    }

    @Test
    void getTotalBalanceByUserId() {
        Double totalBalance = accountRepository.getTotalBalanceByUserId(1L);
        assertEquals(1000.00, totalBalance);
    }

    @Test
    void existsByUserIdAndAccountType() {
        boolean exists = accountRepository.existsByUserIdAndAccountType(1L, AccountType.CURRENT);
        assertTrue(exists);
    }
}