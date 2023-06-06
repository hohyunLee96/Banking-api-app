package nl.inholland.bankingapi.controller;

import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.TransactionType;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/accounts")
@Log
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

//    @PreAuthorize("hasRole('EMPLOYEE')")
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
//    @PreAuthorize("hasRole('CUSTOMER')")
//    @GetMapping("/user/{id}")
//    public ResponseEntity<Object> getAllAccountsByUserId(@PathVariable Long id) {
//        List<AccountGET_DTO> accounts = accountService.getAllAccountsByUserId(id);
//        Double totalBalance = accountService.getTotalBalanceByUserId(id);
//        if(totalBalance == null){
//            totalBalance = 0.0;
//        }
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("accounts", accounts);
//        response.put("totalBalance", totalBalance);
//
//        return ResponseEntity.ok().body(response);
//    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountById(@PathVariable long id) {
        return ResponseEntity.ok().body(accountService.getAccountById(id));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAccount(@PathVariable long id, @RequestBody AccountGET_DTO accountGET_dto) {
        return ResponseEntity.ok().body(accountService.disableAccount(id, accountGET_dto));
    }
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<Object> addAccount(@RequestBody AccountPOST_DTO accountPOST_dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.addAccount(accountPOST_dto));
    }
}
