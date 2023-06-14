package nl.inholland.bankingapi.controller;

import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.dto.AccountGET_DTO;
import nl.inholland.bankingapi.model.dto.AccountPOST_DTO;
import nl.inholland.bankingapi.model.dto.AccountPUT_DTO;
import nl.inholland.bankingapi.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@CrossOrigin("*")
@RestController
@RequestMapping("/accounts")
@Log
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    //    @PreAuthorize("hasRole('EMPLOYEE')")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CUSTOMER')")
    @GetMapping
    public ResponseEntity<Object> getAllAccounts(
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) AccountType accountType,
            @RequestParam(required = false) Double absoluteLimit,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long user) {
        return ResponseEntity.ok(accountService.getAllAccounts(offset, limit, firstName, lastName, accountType, absoluteLimit, isActive, user));
    }
    @GetMapping(params = "totalBalance")
    public ResponseEntity<Double> totalBalance(@RequestParam("totalBalance") Long id) {
        Double totalBalance = accountService.getTotalBalanceByUserId(id);
        // Use the totalBalance parameter in your logic
        // For example, you can perform additional operations or validations
        return ResponseEntity.ok(totalBalance);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/search")
    public ResponseEntity<Object>getIbanWithFirstAndLastNameForCustomer(
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName
    ){
        return ResponseEntity.ok(accountService.getIbanWithFirstAndLastNameForCustomer(offset, limit, firstName, lastName));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountById(@PathVariable long id) {

            return ResponseEntity.ok().body(accountService.getAccountById(id));

    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAccount(@PathVariable long id, @RequestBody AccountPUT_DTO accountPUT_dto) {

            return ResponseEntity.ok().body(accountService.modifyAccount(id, accountPUT_dto));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<Object> addAccount(@RequestBody AccountPOST_DTO accountPOST_dto) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(accountService.addAccount(accountPOST_dto));
    }
}
