package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.*;
import nl.inholland.bankingapi.model.specifications.TransactionSpecifications;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final HttpServletRequest request;
    private final AccountRepository accountRepository;
    private final UserService userService;
    private static final String BANK_IBAN = "NL01INHO0000000001";

    public TransactionService(TransactionRepository transactionRepository
            , AccountService accountService,
                              HttpServletRequest request, AccountRepository accountRepository1,
                              UserService userService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.request = request;
        this.accountRepository = accountRepository1;
        this.userService = userService;
    }

    public List<TransactionGET_DTO> getAllTransactions(Integer page, Integer limit, String fromIban, String toIban, String fromDate, String toDate, Double lessThanAmount, Double greaterThanAmount, Double equalToAmount, TransactionType type, Long performingUser) {
        Pageable pageable = PageRequest.of(page, limit);
        Specification<Transaction> specification = TransactionSpecifications.getSpecifications(fromIban, toIban, fromDate, toDate,
                lessThanAmount, greaterThanAmount, equalToAmount);

        User loggedInUser= userService.getLoggedInUser(request);
        //set all transactions
        List<TransactionGET_DTO> allTransactions = new ArrayList<>();
        //set user transactions
        List<TransactionGET_DTO> userTransactions = new ArrayList<>();
        //for all transactions in the repository
        for (Transaction transaction : transactionRepository.findAll(specification, pageable)) {
            //add all transactions to the list
            allTransactions.add(convertTransactionResponseToDTO(transaction));
            //if the transaction is performed by the logged-in user, add it to the userTransactions list
            if (transaction.getPerformingUser().getId().equals(loggedInUser.getId())) {
                userTransactions.add(convertTransactionResponseToDTO(transaction));
            }
        }

        //if the logged-in user is an employee, then show all transactions else show only user transactions
        if (userService.getLoggedInUser(request).getUserType().equals(UserType.ROLE_CUSTOMER)) {
            return userTransactions;
        } else if (userService.getLoggedInUser(request).getUserType().equals(UserType.ROLE_EMPLOYEE)) {
            return allTransactions;
        }
        return allTransactions;
    }

    public Transaction addTransaction(@NotNull TransactionPOST_DTO transactionPOSTDto) {
        try {
            Account senderAccount = accountService.getAccountByIBAN(transactionPOSTDto.fromIban());
            Account receiverAccount = accountService.getAccountByIBAN(transactionPOSTDto.toIban());

            //check the transaction
            processTransaction(transactionPOSTDto, senderAccount, receiverAccount);
            //transfer money from sender to receiver and update balances
            transferMoney(senderAccount, receiverAccount, transactionPOSTDto.amount());

            //save transaction to transaction repository
            return transactionRepository.save(mapTransactionToPostDTO(transactionPOSTDto));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Transaction could not be completed " + e.getMessage());
        }
    }

    private void transferMoney(Account senderAccount, Account receiverAccount, Double amount) {
        //subtract money from the sender and save
        senderAccount.decreaseBalance(amount);
        receiverAccount.increaseBalance(amount);
        // Save the updated receiver account
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
    }

    public TransactionGET_DTO getTransactionById(long id) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isPresent()) {
            return convertTransactionResponseToDTO((optionalTransaction.get()));
        } else {
            throw new EntityNotFoundException("Transaction with the specified ID not found.");
        }
    }

    private Transaction mapTransactionToPostDTO(TransactionPOST_DTO postDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(postDto.amount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPerformingUser(userService.getUserById(userService.getLoggedInUser(request).getId()));
        transaction.setToIban(accountService.getAccountByIBAN(postDto.toIban()));
        transaction.setFromIban(accountService.getAccountByIBAN(postDto.fromIban()));
        transaction.setType(postDto.type());
        return transaction;
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

   private void processTransaction(TransactionPOST_DTO transaction, Account fromAccount, Account toAccount) {
        User performingUser = userService.getLoggedInUser(request);
        User receiverUser = userService.getUserById(toAccount.getUser().getId());
        User senderUser = userService.getUserById(performingUser.getId());

        if (fromAccount.getIBAN().equals(toAccount.getIBAN())) {
            throw new ApiRequestException("You cannot transfer money to the same account", HttpStatus.BAD_REQUEST);
        }
        if (accountIsSavingsAccount(fromAccount) && !userIsOwnerOfAccount(senderUser, fromAccount) && transaction.type() == TransactionType.WITHDRAWAL) {
            throw new ApiRequestException("You do not own the savings account you are trying to withdraw from", HttpStatus.FORBIDDEN);
        }
        if (accountIsSavingsAccount(toAccount) && !userIsOwnerOfAccount(senderUser, toAccount) && transaction.type() == TransactionType.TRANSFER) {
            throw new ApiRequestException("You do not own the savings account you are trying to transfer to", HttpStatus.FORBIDDEN);
        }
        if (!userIsOwnerOfAccount(senderUser, fromAccount) && (!userIsEmployee(senderUser)) && (!transactionIsWithdrawalOrDeposit(transaction))) {
            throw new ApiRequestException("You are not the owner of the account you are trying to transfer money from", HttpStatus.FORBIDDEN);
        }
        if (!userIsOwnerOfAccount(receiverUser, toAccount) && (!userIsEmployee(senderUser)) && !transactionIsWithdrawalOrDeposit(transaction)) {
            throw new ApiRequestException("You are not the owner of the account you are trying to transfer money to", HttpStatus.FORBIDDEN);
        }
        if (senderUser.getTransactionLimit() < transaction.amount()
                && transaction.type() != TransactionType.DEPOSIT
                && !accountIsSavingsAccount(toAccount)
                && !userIsOwnerOfAccount(senderUser, toAccount)
        ) {
            throw new ApiRequestException("You have exceeded your transaction limit", HttpStatus.FORBIDDEN);
        }
        if ((getSumOfAllTransactionsFromTodayByLoggedInUserAccount() + transaction.amount() > senderUser.getDailyLimit())
                && transaction.type() != TransactionType.DEPOSIT
                && !accountIsSavingsAccount(toAccount)
                && !userIsOwnerOfAccount(senderUser, toAccount)) {
            throw new ApiRequestException("You have exceeded your daily limit", HttpStatus.BAD_REQUEST);
        }
        if (!fromAccount.getIsActive()) {
            throw new ApiRequestException("Sender account cannot be a CLOSED account.", HttpStatus.BAD_REQUEST);
        }
        if (!toAccount.getIsActive()) {
            throw new ApiRequestException("Receiving account cannot be a CLOSED account.", HttpStatus.BAD_REQUEST);
        }
        if ((fromAccount.getBalance() - transaction.amount()) < fromAccount.getAbsoluteLimit())
            throw new ApiRequestException("You have exceeded the absolute limit", HttpStatus.BAD_REQUEST);
    }

    public Transaction withdraw(TransactionWithdrawDTO dto) {
        return addTransaction(new TransactionPOST_DTO(
                dto.fromIban(),
                BANK_IBAN,
                dto.amount(),
                TransactionType.WITHDRAWAL,
                userService.getLoggedInUser(request).getId()));
    }

    public Transaction deposit(TransactionDepositDTO dto) {
        return addTransaction(new TransactionPOST_DTO(
                BANK_IBAN,
                dto.toIban(),
                dto.amount(),
                TransactionType.DEPOSIT,
                userService.getLoggedInUser(request).getId()));
    }

    private Double getSumOfAllTransactionsFromTodayByLoggedInUserAccount() {
        User user = userService.getLoggedInUser(request);
        List<Transaction> transactions = transactionRepository.findAllByPerformingUserAndTimestampBetween(user, LocalDate.now().atTime(0, 0), LocalDate.now().atTime(23, 59));
        double totalAmount = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() != TransactionType.DEPOSIT
                    && transaction.getToIban().getAccountType() != AccountType.SAVINGS
                    && transaction.getFromIban().getAccountType() != AccountType.SAVINGS
            ) {
                totalAmount += transaction.getAmount();
            }
        }
        return totalAmount;
    }

    public Double getDailyTransactionLimitLeft() {
        User user = userService.getLoggedInUser(request);
        return user.getDailyLimit() - getSumOfAllTransactionsFromTodayByLoggedInUserAccount();
    }

    public DailyTransactionDto convertAmountLeftToDailyTransaction() {
        return new DailyTransactionDto(getDailyTransactionLimitLeft());
    }

    private boolean accountIsSavingsAccount(Account account) {
        return account.getAccountType() == AccountType.SAVINGS;
    }

    private boolean userIsEmployee(User user) {
        return user.getUserType() == UserType.ROLE_EMPLOYEE;
    }

    private boolean userIsOwnerOfAccount(User user, Account account) {
        return Objects.equals(user.getId(), account.getUser().getId());
    }

    private boolean transactionIsWithdrawalOrDeposit(TransactionPOST_DTO transaction) {
        return transaction.type() == TransactionType.WITHDRAWAL || transaction.type() == TransactionType.DEPOSIT;
    }
}