package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, ModelMapper modelMapper, AccountRepository accountRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    public List<Transaction> getAllTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    public Transaction addTransaction(TransactionPOST_DTO transactionPOSTDto) {
//        Transaction transaction = mapTransactionToPostDTO(transactionPOSTDto);
//        transaction.setTimestamp(LocalDateTime.now());
//        transaction.setPerformingUser(userRepository.findUserById(transactionPOSTDto.performingUser().getId()));
        return transactionRepository.save(mapTransactionToPostDTO(transactionPOSTDto));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Transaction mapTransactionToGetDTO(TransactionGET_DTO transactionGETDto) {
        return modelMapper.map(transactionGETDto, Transaction.class);
    }


    public Transaction mapTransactionToPostDTO(TransactionPOST_DTO postDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(postDto.amount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPerformingUser(userRepository.findUserById(postDto.performingUser()));
        transaction.setToIban(accountService.getAccountByIBAN(postDto.toIban()));
        transaction.setFromIban(accountService.getAccountByIBAN(postDto.fromIban()));
        transaction.setType(postDto.type());
        return transaction;
    }
}
