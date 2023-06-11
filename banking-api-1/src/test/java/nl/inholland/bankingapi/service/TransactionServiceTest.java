package nl.inholland.bankingapi.service;

import jakarta.servlet.http.HttpServletRequest;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.model.specifications.TransactionSpecifications;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @MockBean
    private TransactionService transactionService;

    @Test
    void getAllTransactions_shouldReturnUserTransactionsForCustomer() {

        // Prepare test data
        Pageable pageable = mock(Pageable.class);
        String fromIban = "NL21INHO0123400081";
        String toIban = "NL21INHO0123400082";
        String fromDate = "2023-06-10T12:21:28.891805";
        String toDate = "2023-06-10T12:22:28.891805";
        Double lessThanAmount = 100.0;
        Double greaterThanAmount = null;
        Double equalToAmount = null;
        TransactionType type = TransactionType.TRANSFER;
        Long performingUser = 123L;
        Date searchDate = new Date();

        User mockUser = new User();
        mockUser.setId(performingUser);
        when(userService.getLoggedInUser(request)).thenReturn(mockUser);

        List<Transaction> mockTransactions = new ArrayList<>();
        Transaction mockTransaction1 = new Transaction();
        mockTransaction1.setId(1L);
        mockTransaction1.setPerformingUser(mockUser);
        mockTransactions.add(mockTransaction1);
        // Add more mocked transactions if needed

        Specification<Transaction> mockSpecification = mock(Specification.class);
        when(TransactionSpecifications.getSpecifications(fromIban, toIban, fromDate, toDate,
                lessThanAmount, null, null)).thenReturn(mockSpecification);
        when(transactionRepository.findAll(mockSpecification, pageable)).thenReturn(mockTransactions);

        // Mock the conversion from Transaction to TransactionGET_DTO
        TransactionGET_DTO mockDto1 = new TransactionGET_DTO(1L, fromIban, toIban,
                100.0, TransactionType.TRANSFER, LocalDateTime.now().toString(), performingUser);
        when(transactionService.convertTransactionResponseToDTO(mockTransaction1)).thenReturn(mockDto1);
        // Mock the conversion for more transactions if needed

        // Invoke the method to be tested
        List<TransactionGET_DTO> result = transactionService.getAllTransactions(fromIban, toIban, fromDate, toDate,
                lessThanAmount, greaterThanAmount, equalToAmount, type, performingUser, searchDate);

        // Perform assertions
        assertThat(result).isNotNull();
        assertThat(result.get(0).transactionId()).isEqualTo(1L);
        // Add more assertions if needed
    }
    @Test
    void addTransaction_shouldCreateTransactionAndTransferMoney() {
        // Prepare test data
        TransactionPOST_DTO transactionDto = new TransactionPOST_DTO("NL21INHO0123400081", "NL21INHO0123400082", 120.0, TransactionType.TRANSFER, 123L);
        Account senderAccount = new Account();
        Account receiverAccount = new Account();
    }

    @Test
    void transferMoneyShouldChangeBalanceOfSenderAndReceiver() {
        // Prepare test data
        TransactionPOST_DTO transactionDto = new TransactionPOST_DTO("NL21INHO0123400081", "NL21INHO0123400082", 120.0, TransactionType.TRANSFER, 123L);
        User senderUser = accountService.getLoggedInUser(request);
        Account senderAccount=new Account();
        Account receiverAccount = new Account();
        senderAccount.setBalance(1000.0);
        receiverAccount.setBalance(1000.0);
        when(accountService.getAccountByIBAN(transactionDto.fromIban())).thenReturn(senderAccount);
        when(accountService.getAccountByIBAN(transactionDto.toIban())).thenReturn(receiverAccount);
        // Invoke the method to be tested
        transactionService.transferMoney(senderAccount, receiverAccount, transactionDto.amount());
        System.out.println(senderAccount.getBalance());
        // Perform assertions
        Assertions.assertEquals(880.0, senderAccount.getBalance());
    }
}
