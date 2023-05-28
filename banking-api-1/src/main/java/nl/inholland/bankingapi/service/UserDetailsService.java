//package nl.inholland.bankingapi.service;
//
//import nl.inholland.bankingapi.repository.UserRepository;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
////responsible for loading user details and constructing the UserDetails object required by Spring Security.
//@Service
//public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
//    private final UserRepository userRepository;
//
//    public UserDetailsService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        final nl.inholland.bankingapi.model.User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
//
//        return User.builder()
//                .username(username)
//                .password(user.getPassword())
////                .authorities(user.getUserType())
//                .build();
//    }
//
//}