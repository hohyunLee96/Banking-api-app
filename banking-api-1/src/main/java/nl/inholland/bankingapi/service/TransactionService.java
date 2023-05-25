package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
//    private final JwtTokenProvider jwtTokenProvider;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction mapTransactionToGetDTO(TransactionGET_DTO transactionGET_dto) {
        Transaction transaction = new Transaction();
        transaction.setId(transactionGET_dto.transactionId());
        transaction.setFromIban(transactionGET_dto.fromIban());
        transaction.setToIban(transactionGET_dto.toIban());
        transaction.setAmount(transactionGET_dto.amount());
        transaction.setPerformingUser(transactionGET_dto.performingUser());
        transaction.setType(TransactionType.valueOf(transactionGET_dto.type()));

        return transaction;
    }

    public Transaction mapTransactionToPostDTO(TransactionPOST_DTO transactionPOSTDto) {

//        Authentication authentication = jwtTokenProvider.getAuthentication(token);
//        int userId = Integer.parseInt(authentication.getName());
        Transaction transaction = new Transaction();
        transaction.setFromIban(transactionPOSTDto.fromIban());
        transaction.setToIban(transactionPOSTDto.toIban());
        transaction.setAmount(transactionPOSTDto.amount());
        transaction.setType(transactionPOSTDto.type());
        transaction.setPerformingUser(transactionPOSTDto.performingUser());
        transaction.setTimestamp(LocalDateTime.now());

        return transaction;

    }
    public List<Transaction> getAllTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    public Transaction addTransaction(TransactionPOST_DTO transactionPOSTDto) {
        return transactionRepository.save(this.mapTransactionToPostDTO(transactionPOSTDto));
    }
}
