package nl.inholland.bankingapi.controller;

import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.model.dto.TransactionWithdrawDTO;
import nl.inholland.bankingapi.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

 class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;
    @Mock
    private TransactionController transactionController;
    private String fromIban;
    private String toIban;
    private String fromDate;
    private String toDate;
    private Double amount;
    private Double lessThanAmount;
    private Double greaterThanAmount;
    private Double equalToAmount;
    private TransactionType type;
    private Long performingUser;
    private Date searchDate;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionController = new TransactionController(transactionService);
        // Mock the necessary parameters for the test
         fromIban = "NL21INHO0123400081";
         toIban = "NL21INHO0123400083";
         fromDate = "2022-01-01";
         toDate = "2022-12-31";
         lessThanAmount = 1000.0;
         greaterThanAmount = 500.0;
         equalToAmount = 750.0;
         type = TransactionType.DEPOSIT;
         performingUser = 12345L;
         searchDate = new Date();


    }

     @Test
     void testGetAllTransactions() {
         // Prepare the test data
         TransactionGET_DTO transactionGETDto = new TransactionGET_DTO(1, "NL21INHO0123400081", "NL21INHO0123400083", 750.0, TransactionType.DEPOSIT, LocalDateTime.now().toString(), 12345L);
         List<TransactionGET_DTO> expectedResponse = Collections.singletonList(transactionGETDto);

         // Mock the response from the transactionService
         when(transactionService.getAllTransactions(fromIban, toIban, fromDate, toDate, lessThanAmount, greaterThanAmount, equalToAmount, type, performingUser, searchDate))
                 .thenReturn(expectedResponse);

         // Call the getAllTransactions method in the transactionController
         ResponseEntity<Object> actualResponse = transactionController.getAllTransactions(fromIban, toIban, lessThanAmount, greaterThanAmount, equalToAmount,
                 fromDate, toDate, type, performingUser, searchDate);

         // Assert that the actual response matches the expected response
         assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
         assertEquals(expectedResponse, actualResponse.getBody());

         // Verify that no exception is thrown and the catch block is executed
         verify(transactionService, times(1)).getAllTransactions(fromIban, toIban, fromDate, toDate, lessThanAmount, greaterThanAmount, equalToAmount, type, performingUser, searchDate);
     }

     @Test
     void deposit() {

     }

     @Test
     void testGetTransactionById() {
         // Prepare the test data
         long transactionId = 1L;
         TransactionGET_DTO expectedResponse = new TransactionGET_DTO(transactionId, "NL21INHO0123400081", "NL21INHO0123400083", 750.0, TransactionType.DEPOSIT, LocalDateTime.now().toString(), 12345L);

         // Mock the response from the transactionService
         when(transactionService.getTransactionById(transactionId)).thenReturn(expectedResponse);

         // Call the getTransactionById method in the transactionController
         ResponseEntity<Object> actualResponse = transactionController.getTransactionById(transactionId);

         // Assert that the actual response matches the expected response
         assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
         assertEquals(expectedResponse, actualResponse.getBody());

         // Verify that no exception is thrown and the catch block is executed
         verify(transactionService, times(1)).getTransactionById(transactionId);
     }
 }
