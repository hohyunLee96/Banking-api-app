package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, AccountRepository accountRepository, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    public List<Transaction> getAllTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    public Transaction addTransaction(TransactionPOST_DTO transactionPOSTDto) {
        Transaction transaction = mapTransactionToPostDTO(transactionPOSTDto);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPerformingUser(userRepository.findUserById(transactionPOSTDto.performingUser().getId()));
        return transactionRepository.save(transaction);
    }

    private Transaction mapTransactionToPostDTO(TransactionPOST_DTO transactionPOSTDto) {
        modelMapper.typeMap(TransactionPOST_DTO.class, Transaction.class)
                .addMappings(mapper -> mapper.skip(Transaction::setTimestamp))
                .addMappings(mapper -> mapper.skip(Transaction::setPerformingUser));

        Transaction transaction = modelMapper.map(transactionPOSTDto, Transaction.class);

        Optional<Account> fromIban = accountRepository.findById(transactionPOSTDto.fromIban());
        transaction.setFromIban(fromIban);

        Account toIban = accountRepository.findAccountById(transactionPOSTDto.getToIban().getAccountId());
        transaction.setToIban(toIban);

        return transaction;
    }
}

