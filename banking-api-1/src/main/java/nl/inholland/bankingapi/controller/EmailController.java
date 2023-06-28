package nl.inholland.bankingapi.controller;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.EmailRequestDTO;
import nl.inholland.bankingapi.repository.UserRepository;
import nl.inholland.bankingapi.service.EmailService;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/forgot")
public class EmailController {

    @Autowired
    private EmailService emailSenderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    // method for email send
    @PostMapping("/sendEmail")
    public ResponseEntity<String> checkSendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {

        User user = userRepository.findByEmail(emailRequestDTO.emailTo());

        if(user == null) {
            throw new EntityNotFoundException("User with email " + emailRequestDTO.emailTo() + " not found.");
        }

        emailSenderService.sendPasswordResetEmailWithLink(user);
        return ResponseEntity.status(201).body(emailRequestDTO.emailTo());
    }

    // method for new password
    @PostMapping("/resetPassword")
    public ResponseEntity<String> checkResetPassword(@RequestBody EmailRequestDTO emailRequestDTO) {
        String resetResult = userService.resetPassword(emailRequestDTO.emailTo(), emailRequestDTO.password());

        if (resetResult != null && !resetResult.isEmpty()) {
            String successMessage = "Password changed successfully";
            return ResponseEntity.status(201).body(successMessage);
        } else {
            String errorMessage = "Failed to reset password";
            throw new ApiRequestException(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }
}

