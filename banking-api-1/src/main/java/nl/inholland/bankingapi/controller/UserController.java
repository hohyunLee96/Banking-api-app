package nl.inholland.bankingapi.controller;

import lombok.extern.java.Log;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("users")
@Log
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}