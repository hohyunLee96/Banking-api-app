package nl.inholland.bankingapi.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionPOST_DTOTest {
    @Test

    void testTransactionToAndTransactionFromHaveValidIban() {
        TransactionPOST_DTO transactionPOST_dto = new TransactionPOST_DTO("NL01INHO0000000001", "NL01INHO0000000012", 100, null);
        assertEquals("NL01INHO0000000001", transactionPOST_dto.fromIban());
        assertEquals("NL01INHO0000000002", transactionPOST_dto.toIban());
    }


}