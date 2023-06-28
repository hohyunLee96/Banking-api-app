package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.TransactionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDTOTest {
    @Test
    void getTransaction() {
        TransactionGET_DTO actualTransactionDTO = new TransactionGET_DTO(1L, "fromIban", "toIban", 10.0, TransactionType.TRANSFER,"timestamp",2L);
        assertEquals("toIban", actualTransactionDTO.toIban());
        assertEquals("fromIban", actualTransactionDTO.fromIban());
        assertEquals(TransactionType.TRANSFER, actualTransactionDTO.type());
        assertEquals(10.0, actualTransactionDTO.amount());
        assertEquals(1L, actualTransactionDTO.transactionId());
        assertEquals("timestamp", actualTransactionDTO.timeStamp());
        assertEquals(2L, actualTransactionDTO.performingUserId());
    }
    @Test
    void postTransaction() {
        TransactionPOST_DTO actualTransactionDTO = new TransactionPOST_DTO("fromIban", "toIban", 10.0, TransactionType.TRANSFER,2L);
        assertEquals("toIban", actualTransactionDTO.toIban());
        assertEquals("fromIban", actualTransactionDTO.fromIban());
        assertEquals(TransactionType.TRANSFER, actualTransactionDTO.type());
        assertEquals(10.0, actualTransactionDTO.amount());
        assertEquals(2L, actualTransactionDTO.performingUser());
    }
    @Test
    void depositTransaction() {
        TransactionDepositDTO actualTransactionDTO = new TransactionDepositDTO("toIban", 10.0);
        assertEquals("toIban", actualTransactionDTO.toIban());
        assertEquals(10.0, actualTransactionDTO.amount());
    }
    @Test
    void withdrawTransaction() {
        TransactionWithdrawDTO actualTransactionDTO = new TransactionWithdrawDTO("fromIban", 10.0);
        assertEquals("fromIban", actualTransactionDTO.fromIban());
        assertEquals(10.0, actualTransactionDTO.amount());
    }
    @Test
    void dailyTransactionLimitDto() {
        DailyTransactionDto actualTransactionDTO = new DailyTransactionDto(10.0);
        assertEquals(10.0, actualTransactionDTO.dailyTransactionLeft());
    }

}