package nl.inholland.bankingapi.controller;

import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.dto.AccountGET_DTO;
import nl.inholland.bankingapi.model.dto.AccountPOST_DTO;
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

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/accounts")
@Log
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasRole('employee')")
    @GetMapping
    public ResponseEntity<Object> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountById(@PathVariable long id) {
        return ResponseEntity.ok().body(accountService.getAccountById(id));
    }
//    @GetMapping("/{id}")
//    public AccountGET_DTO getAccountById(@PathVariable long id) {
//        return accountService.getAccountByUserId(id);
//    }

    @GetMapping("/iban/{firstname}")
    public ResponseEntity<Object> getIBANByUserFirstName(@PathVariable String firstname) {
        return ResponseEntity.ok().body(accountService.getIBANByUserFirstName(firstname));
    }
    @GetMapping("/totalbalance/{id}")
    public Double getTotalBalanceByUserId(@PathVariable Long id) {
        return accountService.getTotalBalanceByUserId(id);
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getAllAccountsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok().body(accountService.getAllAccountsByUserId(id));
    }

    @PostMapping
    public ResponseEntity<Object> addAccount(@RequestBody AccountPOST_DTO accountPOST_dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.addAccount(accountPOST_dto));
    }



}
