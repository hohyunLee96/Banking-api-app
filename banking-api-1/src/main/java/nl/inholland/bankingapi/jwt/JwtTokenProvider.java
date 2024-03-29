package nl.inholland.bankingapi.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${application.token.validity}")
    private long validityInMicroseconds;
    private  final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtKeyProvider jwtKeyProvider;

    // takes username, roles, and validity period as inputs and creates a token
    // with necessary claims and expiration time and signs it with the private key
    // returns the generated token
    public String createToken(String username, UserType roles) {
        //hold information about the user to be included in the token
        Claims tokenClaims = Jwts.claims().setSubject(username);

        //we only provide the role as information to the frontend
        tokenClaims.put("auth", roles.name());

        // We decide on an expiration date
        Date now = new Date();
        Date expiration = calculateExpirationDate(now, validityInMicroseconds);

        if (expiration.getTime() - now.getTime() < 60000) {
            System.out.println("Warning: Token expired in less than 1 minute!");
        }

        // And finally, generate the token and sign it. .compact() then turns it into a string that we can return.
        // configures the JWT with the corresponding claims, issuance time, expiration time, and signature.
        return Jwts.builder()
                .setClaims(tokenClaims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(jwtKeyProvider.getPrivateKey())
                .compact();
    }

    // reusable method to calculate the expiration date
    public Date calculateExpirationDate(Date currentDate, long validityInMicroseconds) {
        long expirationTimeInMilliseconds = currentDate.getTime() + validityInMicroseconds;
        return new Date(expirationTimeInMilliseconds);
    }

    //used in token filter
    public Authentication getAuthentication(String token) {
        // And then get the UserDetails for this user from our service
        // We can then pass the UserDetails back to the caller
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(jwtKeyProvider.getPrivateKey()).build().parseClaimsJws(token);
            String email = claims.getBody().getSubject();
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Bearer token not valid");
        }
    }
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtKeyProvider.getPrivateKey()).build().parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, "Expired or invalid JWT token");
        }
    }

}