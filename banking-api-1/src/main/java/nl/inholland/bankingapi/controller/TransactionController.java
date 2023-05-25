package nl.inholland.bankingapi.controller;

import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("transactions")
@Log
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PostMapping
    public ResponseEntity<Object> addTransaction(TransactionPOST_DTO transactionPOSTDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.addTransaction(transactionPOSTDto));
    }
}
