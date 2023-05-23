package nl.inholland.bankingapi.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.service.JwtService;
import nl.inholland.bankingapi.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(2)
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private UserDetailsServiceImpl userDetailsService;

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
            if (jwtService.isTokenValid(jwt, userDetailsService)) {
                //create an object of type UsernamePasswordAuthenticationToken
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