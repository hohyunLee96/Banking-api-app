package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.model.specifications.TransactionSpecifications;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionCriteriaRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final TransactionCriteriaRepository transactionCriteriaRepository;
    private final TransactionSpecifications transactionSpecifications;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              ModelMapper modelMapper,
                              AccountRepository accountRepository,
                              EntityManager entityManager, AccountService accountService,
                              TransactionCriteriaRepository transactionCriteriaRepository,
                              TransactionSpecifications transactionSpecifications) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.entityManager = entityManager;
        this.accountService = accountService;
        this.transactionCriteriaRepository = transactionCriteriaRepository;
        this.transactionSpecifications = transactionSpecifications;
    }

    public List<TransactionGET_DTO> getAllTransactions(String fromIban, String toIban, String fromDate, String toDate, Double lessThanAmount, Double greaterThanAmount, Double equalToAmount, TransactionType type, Long performingUser) {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Transaction> specification = TransactionSpecifications.getSpecifications(fromIban, toIban, fromDate, toDate, lessThanAmount, greaterThanAmount, equalToAmount, type, performingUser);
        List<TransactionGET_DTO> transactions = new ArrayList<>();
        for (Transaction transaction : transactionRepository.findAll(specification, pageable)) {
            transactions.add(convertTransactionResponseToDTO(transaction));
        }
        return transactions;

    }

    public TransactionGET_DTO convertTransactionResponseToDTO(Transaction transaction) {
        return new TransactionGET_DTO(
                transaction.getId(),
                transaction.getFromIban().getIBAN(),
                transaction.getToIban().getIBAN(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTimestamp(),
                transaction.getPerformingUser().getId()
        );
    }

    public Transaction addTransaction(@org.jetbrains.annotations.NotNull TransactionPOST_DTO transactionPOSTDto) {
        Account senderAccount = accountService.getAccountByIBAN(transactionPOSTDto.fromIban());
        Account receiverAccount = accountService.getAccountByIBAN(transactionPOSTDto.toIban());
        User senderUser = senderAccount.getUser();

        checkTransaction(mapTransactionToPostDTO(transactionPOSTDto), senderAccount, receiverAccount);
        transferMoney(senderAccount, receiverAccount, transactionPOSTDto.amount());
        return transactionRepository.save(mapTransactionToPostDTO(transactionPOSTDto));
    }

    private void transferMoney(Account senderAccount, Account receiverAccount, Double amount) {
        //subtract money from the sender and save
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        //add money to the receiver and save
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
    }

    public List<Transaction> getAllTransactionsByIban(@NotBlank Account iban) {
        return transactionRepository.findAllByFromIban(iban);
    }

    public TransactionGET_DTO getTransactionById(long id) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isPresent()) {
            return convertTransactionResponseToDTO((optionalTransaction.get()));
        } else {
            throw new EntityNotFoundException("Transaction with the specified ID not found.");
        }
    }


    public Double getTotalTransactionAmountByUser(User user) {
        List<Transaction> transactions = transactionRepository.findAllByPerformingUser_Id(user.getId());
        double totalAmount = 0.0;
        for (Transaction transaction : transactions) {
            totalAmount += transaction.getAmount();
        }
        return totalAmount;
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

    private ResponseEntity<String> checkTransaction(Transaction transaction, Account fromAccount, Account toAccount) {
        if (transaction.getAmount() <= 0) {
          return new ResponseEntity<>("You cannot transfer a negative amount of money", HttpStatus.BAD_REQUEST);
        }
        if (fromAccount.getBalance() < transaction.getAmount()) {
            return new ResponseEntity<>("You do not have enough money to perform this transaction", HttpStatus.BAD_REQUEST);
        }
        if (fromAccount.getIBAN().equals(toAccount.getIBAN())) {
          return new ResponseEntity<>("You cannot transfer money to the same account", HttpStatus.BAD_REQUEST);
        }
        if (fromAccount.getUser().getId() != transaction.getPerformingUser().getId()) {
        return new ResponseEntity<>("You are not the owner of the account you are trying to transfer money from", HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(HttpStatus.OK);

    }


}