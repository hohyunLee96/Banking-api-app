package nl.inholland.bankingapi.service;

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
        String recipientAddress = user.getEmail();
        String subject = "Password Reset";
        String resetLink = "http://localhost:5173/resetPassword" ;
        String body = "Dear " + user.getFirstName() + " " + user.getLastName() + ",\n\n"
                + "Please click the following link to reset your password:\n\n"
                + resetLink + "\n\n"
                + "If you did not request a password reset, please ignore this email.\n\n"
                + "Best regards,\n"
                + "Your Application Team";

        sendEmail(recipientAddress, subject, body);
    }
}
