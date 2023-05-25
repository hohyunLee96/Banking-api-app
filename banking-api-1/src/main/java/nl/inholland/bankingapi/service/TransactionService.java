package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.repository.TransactionRepository;
import nl.inholland.bankingapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public List<Transaction> getAllTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    public Transaction addTransaction(TransactionPOST_DTO transactionPOSTDto) {
        return transactionRepository.save(mapTransactionToPostDTO(transactionPOSTDto));
    }

    public Transaction mapTransactionToGetDTO(TransactionGET_DTO transactionGETDto) {
        return modelMapper.map(transactionGETDto, Transaction.class);
    }

    public Transaction mapTransactionToPostDTO(TransactionPOST_DTO transactionPOSTDto) {
        return modelMapper.map(transactionPOSTDto, Transaction.class);
    }
}