package nl.inholland.bankingapi.controller;

import lombok.extern.java.Log;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.dto.TransactionDepositDTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.model.dto.TransactionWithdrawDTO;
import nl.inholland.bankingapi.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin("*")
@RestController
@RequestMapping("transactions")
@Log
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Object> getAllTransactions(
//            @RequestParam(required = false) Integer offset,
//            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String fromIban,
            @RequestParam(required = false) String toIban,
            @RequestParam(required = false) Double lessThanAmount,
            @RequestParam(required = false) Double greaterThanAmount,
            @RequestParam(required = false) Double equalToAmount,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long performingUser
            , @RequestParam(required = false) Date searchDate

    ) {
        try {
            return ResponseEntity.ok(transactionService.getAllTransactions(fromIban, toIban, fromDate, toDate, lessThanAmount, greaterThanAmount, equalToAmount, type, performingUser, searchDate));
        } catch (Exception e) {
            throw new ApiRequestException( e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Object> addTransaction(@RequestBody Transaction transaction) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.convertTransactionResponseToDTO(transactionService.addTransaction(transaction)));
        } catch (Exception e) {
            throw new ApiRequestException( e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Object> deposit(@RequestBody Transaction transaction) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit((transaction)));
        } catch (Exception e) {
            throw new ApiRequestException( e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Object> withdraw(@RequestBody Transaction transaction) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.withdraw((transaction)));
        } catch (Exception e) {
            throw new ApiRequestException( e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Object> getTransactionById(@PathVariable long id) {
        try{
        return ResponseEntity.ok(transactionService.getTransactionById(id));
        }
        catch (Exception e) {
            throw new ApiRequestException( e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
