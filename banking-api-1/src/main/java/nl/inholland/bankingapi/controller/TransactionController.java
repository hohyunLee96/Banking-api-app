package nl.inholland.bankingapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
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

@ControllerAdvice
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
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) String fromIban,
            @RequestParam(required = false) String toIban,
            @RequestParam(required = false) Double lessThanAmount,
            @RequestParam(required = false) Double greaterThanAmount,
            @RequestParam(required = false) Double equalToAmount,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long performingUser
    ) {
        return ResponseEntity.ok(transactionService.getAllTransactions(page,limit,fromIban, toIban, fromDate, toDate, lessThanAmount, greaterThanAmount, equalToAmount, type, performingUser));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Object> addTransaction(@RequestBody TransactionPOST_DTO transactionPOSTDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.convertTransactionResponseToDTO(transactionService.addTransaction(transactionPOSTDto)));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Object> withdraw(@RequestBody TransactionWithdrawDTO transactionWithdrawDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.convertTransactionResponseToDTO(transactionService.withdraw(transactionWithdrawDTO)));
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Object> deposit(@RequestBody TransactionDepositDTO transactionDepositDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.convertTransactionResponseToDTO(transactionService.deposit(transactionDepositDTO)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Object> getTransactionById(@PathVariable long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/dailyTransactionsLeft")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Object> getDailyTransactionsLeft() {
        return ResponseEntity.ok(transactionService.convertAmountLeftToDailyTransaction());
    }
}
