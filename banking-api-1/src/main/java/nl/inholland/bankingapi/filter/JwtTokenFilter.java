package nl.inholland.bankingapi.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(2)
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        //when call is made, we need to pass jwt authentication token inside a header
        //we need to extract the token from the header
        final String authHeader = request.getHeader("Authorization");
        //the jwt token
        final String jwt;
        final String usernameFromToken;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //substring 7 because authHeader is "Bearer " + jwt, so 7 characters
        jwt = authHeader.substring(7);

        usernameFromToken = jwtService.extractUsername(jwt);
        //if we have username and the user is not authenticated yet
        if (usernameFromToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //get user details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(usernameFromToken);
            //check if the token is valid, if it is, then we can authenticate the user
            if (jwtService.isTokenValid(jwt, userDetails)) {
                //create an object of type UsernamePasswordAuthenticationToken
                //pass user details, credentials and authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                //enforce authentication with details of the request
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                //then update the token in the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //always pass on to the next filter
        filterChain.doFilter(request, response);
    }
}

//package nl.inholland.bankingapi.filter;
//
//        import nl.inholland.bankingapi.util.JwtTokenProvider;
//        import io.jsonwebtoken.JwtException;
//        import jakarta.servlet.FilterChain;
//        import jakarta.servlet.ServletException;
//        import jakarta.servlet.http.HttpServletRequest;
//        import jakarta.servlet.http.HttpServletResponse;
//        import org.springframework.security.core.Authentication;
//        import org.springframework.security.core.context.SecurityContextHolder;
//        import org.springframework.stereotype.Component;
//        import org.springframework.web.filter.OncePerRequestFilter;
//
//
//        import java.io.IOException;
//
//@Component
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // First, we'll get the token from the requests authorization header
//        String token = getToken(request);
//
//        // If no token is provided, we'll just continue along the chain.
//        // The framework will automatically return a 403 if the accessed URL required authorization
//        if (token == null) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        else{
//            System.out.println("Token: " + token);
//        }
//
//        try {
//            // If a token was provided, we should  validate it and set the security context
//            // We need a Spring Authentication object to set the Spring Security context
//            Authentication authentication = jwtTokenProvider.getAuthentication(token);
//            // If the token was invalid, the line above will cause an exception
//
//            // Set the context, at this point, the user is authenticated
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            // The exception handling below is not mandatory. If we leave it out, the client will simply receive a 403 status code
//            // The method below gives us a bit more control, by immediately writing a response and then ending the processing of the request
//        } catch (JwtException e) {
//            // JwtException = something is wrong with the JWT (usually means it's invalid)
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Invalid JWT token");
//            response.getWriter().flush();
//            return;
//        } catch (Exception e) {
//            // Exception = something else went wrong, we don't know what
//            // Writing the exception message is probably a bad idea, since it can provide the client with information about potential
//            // security vulnerabilities. We should log it instead.
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.getWriter().write(e.getMessage());
//            response.getWriter().flush();
//            return;
//        }
//
//        // Continue along the filter chain
//        filterChain.doFilter(request, response);
//    }
//    private String getToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}