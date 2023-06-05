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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/accounts")
@Log
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    //    @GetMapping("/{id}")
//    public ResponseEntity<Object> getAccountById(@PathVariable long id, @RequestParam(value = "totalBalance", required = false) Double totalBalance) {
//        return ResponseEntity.ok().body(accountService.getAccountById(id));
//    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountById(@PathVariable long id) {
        return ResponseEntity.ok().body(accountService.getAccountById(id));
    }
    @GetMapping(params = "firstname")
    public ResponseEntity<Object> getIBANByUserFirstName(@RequestParam String firstname) {
        return ResponseEntity.ok().body(accountService.getIBANByUserFirstName(firstname));
    }
    @GetMapping(params = "user")
    public ResponseEntity<Object> getAllAccountsByUserId(@RequestParam Long user) {
        List<Account> accounts = accountService.getAllAccountsByUserId(user);
        double totalBalance = accountService.getTotalBalanceByUserId(user);

        Map<String, Object> response = new HashMap<>();
        response.put("accounts", accounts);
        response.put("totalBalance", totalBalance);

        return ResponseEntity.ok().body(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAccount(@PathVariable long id, @RequestBody AccountGET_DTO accountGET_dto) {
        return ResponseEntity.ok().body(accountService.disableAccount(id, accountGET_dto));
    }

    @PostMapping
    public ResponseEntity<Object> addAccount(@RequestBody AccountPOST_DTO accountPOST_dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.addAccount(accountPOST_dto));
    }
}
