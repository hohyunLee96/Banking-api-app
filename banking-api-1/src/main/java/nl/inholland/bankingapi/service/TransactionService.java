package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction mapTransactionToDTO(TransactionGET_DTO transactionGET_dto){

        Transaction transaction = new Transaction();
        transaction.setId(transactionGET_dto.transactionId());
        transaction.setFromIban(transactionGET_dto.fromIban());
        transaction.setToIban(transactionGET_dto.toIban());
        transaction.setAmount(transactionGET_dto.amount());
        transaction.setType(TransactionType.valueOf(transactionGET_dto.type()));

        return transaction;
    }
}
