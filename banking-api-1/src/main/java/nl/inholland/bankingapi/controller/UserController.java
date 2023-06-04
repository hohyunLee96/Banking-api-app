package nl.inholland.bankingapi.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.UserPOST_DTO;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.http.HttpStatus;
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

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    //GET Returns all the users on the system
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //POST Creates a new user
    @PostMapping
    public ResponseEntity registerUser(@RequestBody UserPOST_DTO dto) {
        try {
            return ResponseEntity.status(201).body(
                    userService.registerUser(dto));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    //DELETE Deletes a user of specified id
    @DeleteMapping("/{id}")
    public ResponseEntity deleteGuitar(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.status(204).body(null);
        } catch (Exception e) {
            throw e;
        }
    }

    //GET Retrieves a user of specified id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }
    @GetMapping(params = "hasAccount")
    public ResponseEntity<Object> getUsersWithoutAccount(@RequestParam Boolean hasAccount) {
        return ResponseEntity.ok().body(userService.getUsersWithoutAccount(hasAccount));
    }

    //PUT Updates an existing user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserPOST_DTO dto) {
        try {
            User updatedUser = userService.updateUser(id, dto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}