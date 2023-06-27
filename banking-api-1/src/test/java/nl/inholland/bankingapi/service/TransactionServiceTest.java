package nl.inholland.bankingapi.service;

import jakarta.servlet.http.HttpServletRequest;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.*;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class TransactionServiceTest {

    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private UserService userService;
    @MockBean
    AccountService accountService;
    @Mock
    private HttpServletRequest request;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    private Account senderAccount, receiverAccount, savingsAccount;
    private User performinUser, customer;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        performinUser = new User("performingUser@email.com", "1234", "Performing", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 10000.00, 10000.00, true);
        performinUser.setId(1L);
        customer = new User("customer@email.com", "1234", "Customer", "Customer", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 10000.00, 10000.00, true);
        senderAccount = new Account(performinUser, "NL21INHO0123400081", 1000.00, 0.00, AccountType.CURRENT, true);
        receiverAccount = new Account(customer, "NL21INHO0123400082", 1000.00, 0.00, AccountType.CURRENT, true);
        transaction = new Transaction(senderAccount, receiverAccount, 100.00, LocalDateTime.now(), TransactionType.TRANSFER, performinUser);
        savingsAccount = new Account(performinUser, "NL21INHO0123400083", 1000.00, 0.00, AccountType.SAVINGS, true);
    }

    @Test
    void createTransaction() {
        User performingUser = this.performinUser;
        Account senderAccount = this.senderAccount;
        Account receiverAccount = this.receiverAccount;
        TransactionType transactionType = TransactionType.TRANSFER;
        double amount = 100.00;

        Transaction transaction1 = new Transaction(senderAccount, receiverAccount, amount, LocalDateTime.now(), transactionType, performingUser);

        assertEquals(transactionType, transaction1.getType());
        assertEquals(100.00, transaction1.getAmount(), 0.00);
        assertEquals(senderAccount, transaction1.getFromIban());
        assertEquals(receiverAccount, transaction1.getToIban());
        assertEquals(performingUser, transaction1.getPerformingUser());
        assertEquals(LocalDateTime.now().getYear(), transaction.getTimestamp().getYear());
        assertEquals(LocalDateTime.now().getMonth(), transaction.getTimestamp().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), transaction.getTimestamp().getDayOfMonth());
        assertEquals(LocalDateTime.now().getHour(), transaction.getTimestamp().getHour());
        assertEquals(LocalDateTime.now().getMinute(), transaction.getTimestamp().getMinute());
        assertEquals(LocalDateTime.now().getSecond(), transaction.getTimestamp().getSecond());
    }

    @Test
    void addTransaction() {
        TransactionPOST_DTO transactionPOST_dto = new TransactionPOST_DTO("NL21INHO0123400081", "NL21INHO0123400082", 100.00, TransactionType.TRANSFER, 1L);

        when(transactionService.addTransaction(transactionPOST_dto)).thenReturn(transaction);
        Assertions.assertEquals(transaction.getFromIban().getIBAN(), transactionPOST_dto.fromIban());
        Assertions.assertEquals(transaction.getToIban().getIBAN(), transactionPOST_dto.toIban());
        Assertions.assertEquals(transaction.getAmount(), transactionPOST_dto.amount(), 0.00);
        Assertions.assertEquals(transaction.getType(), transactionPOST_dto.type());
    }

    @Test
    void getDailyTransactionLimitLeft() {
        when(transactionService.getDailyTransactionLimitLeft()).thenReturn(1000.00);
        Assertions.assertEquals(1000.00, transactionService.getDailyTransactionLimitLeft(), 0.00);
    }

    @Test
    void convertAmountLeftToDailyTransaction() {
        DailyTransactionDto dailyTransactionDto = new DailyTransactionDto(1000.00);
        when(transactionService.convertAmountLeftToDailyTransaction()).thenReturn(dailyTransactionDto);
        Assertions.assertEquals(1000.00, dailyTransactionDto.dailyTransactionLeft());
    }
    @Test
    void withdrawShouldReturnTransaction() {
        TransactionWithdrawDTO withdrawDTO = new TransactionWithdrawDTO("NL21INHO0123400081", 100.00);
        when(transactionService.withdraw(withdrawDTO)).thenReturn(transaction);
        Assertions.assertEquals(transaction.getFromIban().getIBAN(), withdrawDTO.fromIban());
        Assertions.assertEquals(transaction.getAmount(), withdrawDTO.amount(), 0.00);
    }

    @Test
    void depositShouldReturnTransaction() {
        TransactionDepositDTO depositDTO = new TransactionDepositDTO("NL21INHO0123400081", 100.00);
        when(transactionService.deposit(depositDTO)).thenReturn(transaction);
        Assertions.assertEquals(transaction.getFromIban().getIBAN(), depositDTO.toIban());
        Assertions.assertEquals(transaction.getAmount(), depositDTO.amount(), 0.00);
    }

    @Test
    void getTransactionByIdReturnsTransaction() {
        transactionRepository.save(transaction);
        TransactionGET_DTO transactionGET_dto = new TransactionGET_DTO(1L, "NL21INHO0123400081", "NL21INHO0123400082", 100.00, TransactionType.TRANSFER, LocalDateTime.now().toString(), 1L);
        when(transactionService.getTransactionById(1L)).thenReturn(transactionGET_dto);
        Assertions.assertEquals(transactionGET_dto.fromIban(), transaction.getFromIban().getIBAN());
        Assertions.assertEquals(transactionGET_dto.toIban(), transaction.getToIban().getIBAN());
        Assertions.assertEquals(transactionGET_dto.amount(), transaction.getAmount(), 0.00);
        Assertions.assertEquals(transactionGET_dto.type(), transaction.getType());
        Assertions.assertEquals(transactionGET_dto.performingUserId(), transaction.getPerformingUser().getId());
    }
}

