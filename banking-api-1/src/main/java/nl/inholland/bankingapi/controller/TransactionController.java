package nl.inholland.bankingapi.controller;

import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.TransactionSearchCriteria;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.service.TransactionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Object> getAllTransactions(
//            @RequestParam(required = false) Integer offset,
//            @RequestParam(required = false) Integer limit,
            @RequestParam( required = false) String fromIban,
            @RequestParam( required = false) String toIban,
            @RequestParam( required = false) Double lessThanAmount,
            @RequestParam( required = false) Double greaterThanAmount,
            @RequestParam( required = false) Double equalToAmount,
            @RequestParam( required = false) String fromDate,
            @RequestParam( required = false) String toDate,
            @RequestParam( required = false) TransactionType type,
            @RequestParam( required = false) Long performingUser

    ) {
        return ResponseEntity.ok(transactionService.getAllTransactions( fromIban,toIban, fromDate, toDate, lessThanAmount, greaterThanAmount, equalToAmount, type, performingUser));
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
