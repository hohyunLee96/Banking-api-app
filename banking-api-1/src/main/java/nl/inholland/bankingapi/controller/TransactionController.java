package nl.inholland.bankingapi.controller;

import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("transactions")
@Log
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @GetMapping
    public ResponseEntity<Object> getAllTransactions(@RequestParam(value = "offset", required = false)Integer offset, @RequestParam(value = "limit", required = false)Integer limit) {
        return ResponseEntity.ok(transactionService.getAllTransactions(offset, limit));
    }

    @PostMapping
    public ResponseEntity<Object> addTransaction(@RequestBody TransactionPOST_DTO transactionPOSTDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.addTransaction(transactionPOSTDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTransactionById(@PathVariable long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
}
