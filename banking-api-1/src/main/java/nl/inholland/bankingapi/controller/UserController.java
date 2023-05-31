package nl.inholland.bankingapi.controller;
import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.dto.LoginRequestDTO;
import nl.inholland.bankingapi.model.dto.RegisterRequestDTO;
import nl.inholland.bankingapi.model.dto.ResponseTokenDTO;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

//    @PreAuthorize("hasRole('USER')")
    @PostMapping("/login")
    public Object login(@RequestBody LoginRequestDTO loginRequestDTO) throws AuthenticationException {
        return ResponseEntity.ok().body(new ResponseTokenDTO(
                userService.login(loginRequestDTO.email(), loginRequestDTO.password())
        ));
    }

}