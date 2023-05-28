package nl.inholland.bankingapi.service;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.repository.AccountRepository;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        return transactionRepository.findAll(pageable).getContent();

        //TODO: correct the offset because it skips 10 now
    }

    public Transaction addTransaction(TransactionPOST_DTO transactionPOSTDto) {
        Account senderAccount = accountService.getAccountByIBAN(transactionPOSTDto.fromIban());
        Account receiverAccount = accountService.getAccountByIBAN(transactionPOSTDto.toIban());
        User senderUser = senderAccount.getUser();

        if (transactionPOSTDto.fromIban() == null) {
            throw new IllegalArgumentException("Error retrieving sending account");
        }
        if (transactionPOSTDto.toIban() == null) {
            throw new IllegalArgumentException("Error retrieving receiving account");
        }
        if (transactionPOSTDto.amount() <= 0) {
            throw new IllegalArgumentException("Invalid amount provided");
        }
        if (senderAccount.getBalance() < transactionPOSTDto.amount()) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        if (senderUser.getDailyLimit() != null && this.getTotalTransactionAmountByUser(senderUser) + transactionPOSTDto.amount() > senderUser.getDailyLimit())
            throw new IllegalArgumentException("Daily limit amount exceeded!");
        if (senderAccount.getAccountType() == AccountType.SAVINGS || receiverAccount.getAccountType() == AccountType.SAVINGS && (senderAccount.getUser() != receiverAccount.getUser())) {
            throw new IllegalArgumentException("You can only transfer money between your own accounts");
        }

//        senderUser.setCurrentTransactionsAmount(senderUser.getCurrentTransactionsAmount() + transaction.getAmount());

        return transactionRepository.save(mapTransactionToPostDTO(transactionPOSTDto));
    }

    public Transaction mapTransactionToGetDTO(TransactionGET_DTO transactionGETDto) {
        return modelMapper.map(transactionGETDto, Transaction.class);
    }

    public Transaction mapTransactionToPostDTO(TransactionPOST_DTO postDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(postDto.amount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPerformingUser(userRepository.getUserById(postDto.performingUser()));
        transaction.setToIban(accountService.getAccountByIBAN(postDto.toIban()));
        transaction.setFromIban(accountService.getAccountByIBAN(postDto.fromIban()));
        transaction.setType(postDto.type());
        return transaction;
    }

    public Transaction getTransactionById(long id) {
        //check if id exists
        if (transactionRepository.findById(id).isEmpty())
            throw new EntityNotFoundException("Transaction with the specified ID not found.");
        return transactionRepository.findById(id).get();
    }

    public Double getTotalTransactionAmountByUser(User user) {
        List<Transaction> transactions = transactionRepository.findAllByPerformingUser_Id(user.getId());
        double totalAmount = 0.0;
        for (Transaction transaction : transactions) {
            totalAmount += transaction.getAmount();
        }
        return totalAmount;
    }

    private void transferMoney(Account senderAccount, Account receiverAccount, Double amount) {
        //subtract money from the sender and save
        senderAccount.setBalance(senderAccount.getBalance() - amount);

        //add money to the receiver and save
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);
    }
}
