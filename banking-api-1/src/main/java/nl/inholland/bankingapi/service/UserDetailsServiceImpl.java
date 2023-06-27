package nl.inholland.bankingapi.service;

import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //user details are loaded from the repository based on the provided email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //If a user with the given email is found, it will be wrapped in an Optional<User> object and returned
        final User user = userRepository.findUserByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User '" + email + "' not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(user.getPassword())
                .authorities(user.getUserType())
                .build();

    }
}
