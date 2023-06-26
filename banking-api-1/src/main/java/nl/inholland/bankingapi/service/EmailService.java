package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.ConfirmationToken;
import nl.inholland.bankingapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    String recipientAddressPasswordReset = "";

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        System.out.println("Email sent to: " + to);
    }

    public void sendPasswordResetEmailWithLink(User user) {
        recipientAddressPasswordReset = user.getEmail();

        String subject = "Password Reset";
        String resetLink = "http://localhost:5173/resetPassword?email=" + user.getEmail();
        String body = "Dear " + user.getFirstName() + " " + user.getLastName() + ",\n\n"
                + "Please click the following link to reset your password:\n\n"
                + resetLink + "\n\n"
                + "If you did not request a password reset, please ignore this email.\n\n"
                + "Best regards,\n"
                + "Your Application Team";

        sendEmail(recipientAddressPasswordReset, subject, body);
    }

    public void sendEmailVerificationWithLink(User user, ConfirmationToken confirmationToken) {
        String recipientAddress = user.getEmail();

        String subject = "Email Verification";
        String resetLink = "http://localhost:5173/confirmAccount?token=" + confirmationToken.getConfirmationToken();
        String body = "Dear " + user.getFirstName() + " " + user.getLastName() + ",\n\n"
                + "Please click the following link to verify your email:\n\n"
                + resetLink + "\n\n"
                + "This step is necessary in order to be registered, to ensure user authenticity, " +
                "prevent fake accounts, maintain data integrity, comply with regulations.\n\n"
                + "Best regards,\n"
                + "Your Application Team";

        sendEmail(recipientAddress, subject, body);
    }

}
