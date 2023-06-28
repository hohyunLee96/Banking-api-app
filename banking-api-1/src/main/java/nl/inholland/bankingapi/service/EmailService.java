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
        // create a SimpleMailMessage object and set the relevant fields
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        System.out.println("Email sent to: " + to);
    }

    public void sendPasswordResetEmailWithLink(User user) {
        // get user email to send password reset link
        recipientAddressPasswordReset = user.getEmail();

        String subject = "Password Reset";
        // Get the email address from the User object
        // NOTE: access to email link gets denied if Backend localhost is used instead of Frontend localhost
        String resetLink = "http://localhost:5173/resetPassword?email=" + user.getEmail();
        String body = "Dear " + user.getFirstName() + " " + user.getLastName() + ",\n\n"
                + "Please click the following link to reset your password:\n\n"
                + resetLink + "\n\n"
                + "If you did not request a password reset, please ignore this email.\n\n"
                + "Best regards,\n"
                + "Your Application Team";
        // set the recipient address, subject and body of the email
        // and send the email
        sendEmail(recipientAddressPasswordReset, subject, body);
    }

    public void sendEmailVerificationWithLink(ConfirmationToken confirmationToken) {
        // get user email to send email verification link
        String recipientAddress = confirmationToken.getUser().getEmail();

        String subject = "Email Verification";
        // Get the token value from the ConfirmationToken object
        String verifyEmailLink = "http://localhost:5173/confirmAccount?token=" + confirmationToken.getConfirmationToken();
        String body = "Dear " + confirmationToken.getUser().getFirstName() + " " + confirmationToken.getUser().getLastName() + ",\n\n"
                + "Please click the following link to verify your email:\n\n"
                // send the link with the token value
                + verifyEmailLink + "\n\n"
                + "This step is necessary in order to be registered, to ensure user authenticity, " +
                "prevent fake accounts, maintain data integrity, comply with regulations.\n\n"
                + "Best regards,\n"
                + "Your Application Team";
        // set the recipient address, subject and body of the email, then
        // send the email with the link and token value to the user email address from the ConfirmationToken object
        sendEmail(recipientAddress, subject, body);
    }

}
