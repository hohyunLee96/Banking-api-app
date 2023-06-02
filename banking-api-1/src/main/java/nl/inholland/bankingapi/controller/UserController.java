package nl.inholland.bankingapi.controller;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.LoginRequestDTO;
import nl.inholland.bankingapi.model.dto.RegisterRequestDTO;
import nl.inholland.bankingapi.model.dto.ResponseTokenDTO;
import nl.inholland.bankingapi.model.dto.UserPOST_DTO;
import nl.inholland.bankingapi.service.UserService;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("users")
@Log
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity registerUser(@RequestBody UserPOST_DTO dto) {
        try {
            return ResponseEntity.status(201).body(
                    userService.registerUser(dto));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteGuitar(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.status(204).body(null);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequestDTO loginRequestDTO) throws AuthenticationException {
        return ResponseEntity.ok().body(new ResponseTokenDTO(
                userService.login(loginRequestDTO.email(), loginRequestDTO.password())
        ));
    }

    @GetMapping("/getUserByAccount/{id}")
    public ResponseEntity<User> getUserByAccount(@PathVariable("id") Long id) {
        User user = userService.getUserByAccountAccountId(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
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