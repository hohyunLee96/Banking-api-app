package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.filter.JwtTokenFilter;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final HttpServletRequest request;

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenFilter jwtTokenFilter;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              ModelMapper modelMapper,
                              AccountRepository accountRepository,
                              EntityManager entityManager, AccountService accountService,
                              TransactionCriteriaRepository transactionCriteriaRepository,
                              TransactionSpecifications transactionSpecifications, HttpServletRequest request, AccountRepository accountRepository1, UserService userService, JwtTokenProvider jwtTokenProvider, JwtTokenFilter jwtTokenFilter) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.entityManager = entityManager;
        this.accountService = accountService;
        this.transactionCriteriaRepository = transactionCriteriaRepository;
        this.transactionSpecifications = transactionSpecifications;
        this.request = request;
        this.accountRepository = accountRepository1;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    public List<TransactionGET_DTO> getAllTransactions(String fromIban, String toIban, String fromDate, String toDate, Double lessThanAmount, Double greaterThanAmount, Double equalToAmount, TransactionType type, Long performingUser) {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Transaction> specification = TransactionSpecifications.getSpecifications(fromIban, toIban, fromDate, toDate, lessThanAmount, greaterThanAmount, equalToAmount, type, performingUser);
        List<TransactionGET_DTO> transactions = new ArrayList<>();
        for (Transaction transaction : transactionRepository.findAll(specification, pageable)) {
            transactions.add(convertTransactionResponseToDTO(transaction));
        }
        getSumOfAllTransactionsFromTodayByIban(accountRepository.findAccountByIBAN(fromIban));
        return transactions;
    }

    public TransactionGET_DTO convertTransactionResponseToDTO(Transaction transaction) {
        return new TransactionGET_DTO(
                transaction.getId(),
                transaction.getFromIban().getIBAN(),
                transaction.getToIban().getIBAN(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTimestamp().toString(),
                transaction.getPerformingUser().getId()
        );
    }

    public Transaction addTransaction(@org.jetbrains.annotations.NotNull TransactionPOST_DTO transactionPOSTDto) {
        try {
            Account senderAccount = accountService.getAccountByIBAN(transactionPOSTDto.fromIban());
            Account receiverAccount = accountService.getAccountByIBAN(transactionPOSTDto.toIban());

            //transfer money from sender to receiver and update balances
            checkTransaction(transactionPOSTDto, senderAccount, receiverAccount);
            transferMoney(senderAccount, receiverAccount, transactionPOSTDto.amount());

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


    public Transaction mapTransactionToPostDTO(TransactionPOST_DTO postDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(postDto.amount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPerformingUser(userService.getUserById(getLoggedInUser(request).getId()));
        transaction.setToIban(accountService.getAccountByIBAN(postDto.toIban()));
        transaction.setFromIban(accountService.getAccountByIBAN(postDto.fromIban()));
        transaction.setType(postDto.type());
        return transaction;
    }

    private void checkTransaction(TransactionPOST_DTO transaction, Account fromAccount, Account toAccount) {
        User perfomingUser = getLoggedInUser(request);
        User receiverUser = userService.getUserById(toAccount.getUser().getId());
        User senderUser = userService.getUserById(perfomingUser.getId());
        if (transaction.amount() <= 0) {
            throw new ApiRequestException("Amounts cannot be 0 or less", HttpStatus.NOT_ACCEPTABLE);
        }
        if (fromAccount.getBalance() < transaction.amount()) {
            throw new ApiRequestException("You do not have enough money to perform this transaction", HttpStatus.BAD_REQUEST);
        }
        if (fromAccount.getIBAN().equals(toAccount.getIBAN())) {
            throw new ApiRequestException("You cannot transfer money to the same account", HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(fromAccount.getUser().getId(), transaction.performingUser()) && perfomingUser.getUserType() != UserType.ROLE_EMPLOYEE) {
            throw new ApiRequestException("You are not the owner of the account you are trying to transfer money from", HttpStatus.FORBIDDEN);
        }
        if (!userIsEmployee(senderUser) && (accountIsSavingsAccount(toAccount) || accountIsSavingsAccount(fromAccount))
                && senderUser.getId() != receiverUser.getId()) {
            throw new ApiRequestException("Savings account does not belong to user", HttpStatus.FORBIDDEN);
        }
        if (fromAccount.getUser().getDailyLimit() < transaction.amount()) {
            throw new ApiRequestException("You have exceeded your daily limit", HttpStatus.BAD_REQUEST);
        }
        if (fromAccount.getUser().getTransactionLimit() < transaction.amount()) {
            throw new ApiRequestException("You have exceeded your transaction limit", HttpStatus.FORBIDDEN);
        }
        if ((getSumOfAllTransactionsFromTodayByIban(fromAccount) + transaction.amount()) > fromAccount.getUser().getDailyLimit()) {
            throw new ApiRequestException("You have exceeded your daily transaction limit", HttpStatus.BAD_REQUEST);
        }
        if (!fromAccount.isActive()) {
            throw new ApiRequestException("Receiver account cannot be a CLOSED account.", HttpStatus.BAD_REQUEST);
        }
        if (!toAccount.isActive()) {
            throw new ApiRequestException("Receiving account cannot be a CLOSED account.", HttpStatus.BAD_REQUEST);
        }
        if (((fromAccount.getBalance()) - transaction.amount()) < toAccount.getAbsoluteLimit())
            throw new ApiRequestException("You can't have that little money in your account!", HttpStatus.BAD_REQUEST);

    }

    public User getLoggedInUser(HttpServletRequest request) {
        // Get JWT token and the information of the authenticated user
        String receivedToken = jwtTokenFilter.getToken(request);
        jwtTokenProvider.validateToken(receivedToken);
        Authentication authenticatedUserUsername = jwtTokenProvider.getAuthentication(receivedToken);
        String userEmail = authenticatedUserUsername.getName();
        return userRepository.findUserByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }

    private Double getSumOfAllTransactionsFromTodayByIban(Account iban) {
        List<Transaction> dailyTransactions = transactionRepository.findAllByFromIbanAndTimestamp(iban, LocalDateTime.now());
        double totalAmount = 0.0;
        for (Transaction transaction : dailyTransactions) {
            totalAmount += transaction.getAmount();
        }
        return totalAmount;
    }


    private boolean accountIsSavingsAccount(Account account) {
        return account.getAccountType() == AccountType.SAVINGS;
    }

    private boolean userIsEmployee(User user) {
        return user.getUserType() == UserType.ROLE_EMPLOYEE;
    }

}