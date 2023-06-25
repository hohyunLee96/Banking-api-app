package nl.inholland.bankingapi.controller;

import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.EmailRequestDTO;
import nl.inholland.bankingapi.model.dto.ResetPasswordRequestDTO;
import nl.inholland.bankingapi.repository.UserRepository;
import nl.inholland.bankingapi.service.EmailService;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/forgot")
public class ForgotController {

    @Autowired
    private EmailService emailSenderService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Connection Established!");
    }

    // method for Email Enter Send
    @PostMapping("/sendEmail")
    public ResponseEntity<String> checkSendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {

        User user = userRepository.findByEmail(emailRequestDTO.emailTo());
        emailSenderService.sendPasswordResetEmailWithLink(user);
        return ResponseEntity.ok("Email sent successfully.");
    }

    // method for New Password
    @PostMapping("/resetPassword")
    public ResponseEntity<String> checkResetPassword(@RequestBody EmailRequestDTO emailRequestDTO, BCryptPasswordEncoder password) {
        String resetResult = userService.resetPassword(emailRequestDTO.emailTo(), password.toString());

        if (resetResult != null && !resetResult.isEmpty()) {
            String successMessage = "Password changed successfully";
            System.out.println(successMessage);

            return ResponseEntity.ok(successMessage);
        } else {
            String errorMessage = "Failed to reset password";
            System.out.println(errorMessage);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}

