//package nl.inholland.bankingapi.filter;
//
//import nl.inholland.bankingapi.util.JwtTokenProvider;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//@Component
//@Order(2)
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    private JwtTokenProvider tokenProvider;
//
//    @Override
//    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
//        String token = request.getHeader("Authorization");
//        if (token == null) {
//            doFilterInternal(request, response, filterChain);
//        }
//
//        try {
//            Authentication authentication = tokenProvider.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//    }
//
//    private String getToken(HttpServletRequest request) {
//        String token = request.getHeader("Authorization")
//                .substring(7);
//        if (token != null) {
//            token = token.replace("Bearer ", "");
//        }
//        return token;
//    }
//}