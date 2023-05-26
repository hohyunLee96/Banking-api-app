package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    public List<Transaction> getAllTransactions(Integer offset, Integer limit) {
        if (offset == null || offset < 0)
            offset = 0;

        if (limit == null || limit < 0)
            limit = 20;

        Pageable pageable = PageRequest.of(offset, limit);
        return  transactionRepository.findAll(pageable).getContent();

        //TODO: correct the offset because it skips 10 now
    }
    public Transaction addTransaction(TransactionPOST_DTO transactionPOSTDto) {
        return transactionRepository.save(mapTransactionToPostDTO(transactionPOSTDto));
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

    public Transaction getTransactionById(long id) {
        //check if id exists
        if (transactionRepository.findById(id).isEmpty())
            throw new ApiRequestException("Transaction with the specified ID not found.", HttpStatus.BAD_REQUEST);
        return transactionRepository.findById(id).get();
    }
}
