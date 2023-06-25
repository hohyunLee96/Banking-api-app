package nl.inholland.bankingapi.controller;

import ch.qos.logback.core.model.Model;
import nl.inholland.bankingapi.model.ResetPasswordData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/password")
public class PasswordResetController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CustomerAccountService customerAccountService;

    @PostMapping("/request")
    public String resetPassword(final ResetPasswordData forgotPasswordForm, RedirectAttributes redirectAttributes) {
        return "";
    }

    @GetMapping("/change")
    public String changePassword(@RequestParam(required = false) String token, final RedirectAttributes redirectAttributes
            , final Model model ) {
        return "";
    }

    @PostMapping("/change")
    public String changePassword(final ResetPasswordData data, final Model model){
        return "";
    }
}
