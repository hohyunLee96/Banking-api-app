package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.model.specifications.TransactionSpecifications;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionCriteriaRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private final AccountRepository accountRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              ModelMapper modelMapper,
                              AccountRepository accountRepository,
                              EntityManager entityManager, AccountService accountService,
                              TransactionCriteriaRepository transactionCriteriaRepository,
                              TransactionSpecifications transactionSpecifications, AccountRepository accountRepository1, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.entityManager = entityManager;
        this.accountService = accountService;
        this.transactionCriteriaRepository = transactionCriteriaRepository;
        this.transactionSpecifications = transactionSpecifications;
        this.accountRepository = accountRepository1;
        this.userService = userService;
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
        try {
            Account senderAccount = accountService.getAccountByIBAN(transactionPOSTDto.fromIban());
            Account receiverAccount = accountService.getAccountByIBAN(transactionPOSTDto.toIban());

            //transfer money from sender to receiver and update balances
            transferMoney(senderAccount, receiverAccount, transactionPOSTDto.amount());
            checkTransaction(transactionPOSTDto, senderAccount, receiverAccount);
            //save transaction to transaction repository
            return transactionRepository.save(mapTransactionToPostDTO(transactionPOSTDto));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Transaction could not be completed " + e.getMessage());
        }
    }

    private void transferMoney(Account senderAccount, Account receiverAccount, Double amount) {
        //subtract money from the sender and save
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
        // Save the updated receiver account
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

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
        transaction.setPerformingUser(userService.getUserById(postDto.performingUser()));
        transaction.setToIban(accountService.getAccountByIBAN(postDto.toIban()));
        transaction.setFromIban(accountService.getAccountByIBAN(postDto.fromIban()));
        transaction.setType(postDto.type());
        return transaction;
    }

    private void checkTransaction(TransactionPOST_DTO transaction, Account fromAccount, Account toAccount) {
        User senderUser = userService.getUserById(transaction.performingUser());
        User receiverUser = userService.getUserById(toAccount.getUser().getId());

        if (transaction.amount() <= 0) {
            throw new ApiRequestException("You cannot transfer a negative amount of money", HttpStatus.BAD_REQUEST);
        }
        if (fromAccount.getBalance() < transaction.amount()) {
            throw new ApiRequestException("You do not have enough money to perform this transaction", HttpStatus.BAD_REQUEST);
        }
        if (fromAccount.getIBAN().equals(toAccount.getIBAN())) {
            throw new ApiRequestException("You cannot transfer money to the same account", HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(fromAccount.getUser().getId(), transaction.performingUser())) {
            throw new ApiRequestException("You are not the owner of the account you are trying to transfer money from", HttpStatus.BAD_REQUEST);
        }
        if (toAccount.getAccountType() == AccountType.SAVINGS || fromAccount.getAccountType() == AccountType.SAVINGS) {
            if (senderUser != receiverUser)
                throw new ApiRequestException("Savings account does not belong to user", HttpStatus.BAD_REQUEST);
        }
//        if (transaction.getToIban().getIsActive() == AccountStatus.CLOSED)
//            throw new ApiRequestException("Account cannot be a CLOSED account.", HttpStatus.BAD_REQUEST);

        if (((fromAccount.getBalance())- transaction.amount()) < toAccount.getAbsoluteLimit())
            throw new ApiRequestException("You can't have that little money in your account!", HttpStatus.BAD_REQUEST);

    }


}