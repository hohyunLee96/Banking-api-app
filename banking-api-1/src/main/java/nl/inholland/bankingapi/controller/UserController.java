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
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("users")
@Log
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //GET Returns all the users on the system
    @GetMapping
    public ResponseEntity<Object> getAllUsers(
            //@RequestParam( required = false) Integer offset,
            //@RequestParam( required = false) Integer limit,
            @RequestParam( required = false) String email,
            @RequestParam( required = false) String firstName,
            @RequestParam( required = false) String lastName,
            @RequestParam( required = false) String birthDate,
            @RequestParam( required = false) String postalCode,
            @RequestParam( required = false) String address,
            @RequestParam( required = false) String city,
            @RequestParam( required = false) String phoneNumber,
            @RequestParam( required = false) String userType,
            @RequestParam( required = false) boolean hasAccount
    ){
        return ResponseEntity.ok(userService.getAllUsers(firstName, lastName, hasAccount, email, birthDate, postalCode, address, city, phoneNumber, userType));
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
            return ResponseEntity.ok().body(userService.updateUser(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequestDTO loginRequestDTO) throws AuthenticationException {
        return ResponseEntity.ok().body(new ResponseTokenDTO(
                userService.login(loginRequestDTO.email(), loginRequestDTO.password())
        ));
    }
}