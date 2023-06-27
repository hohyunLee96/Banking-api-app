package nl.inholland.bankingapi.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.inholland.bankingapi.filter.JwtTokenFilter;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.*;

class JwtTokenFilterTest {

    private JwtTokenFilter jwtTokenFilter;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider);
    }

    //valid token is provided in the request header
    //sets the authentication in the SecurityContext
    //also verifies that the filter chain continues to execute after setting the authentication
    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSIsImF1dGgiOiJST0xFX0NVU1RPTUVSIiwiaWF0IjoxNjg2MzAwMTMxLCJleHAiOjE2ODYzMDAxMzF9.BKx5_i-hpC11TEKdPKZS2aopH9pdrcTDKuXhlYUvfnQ9iw2vG0mb-m95ZMrQ4YC3ZYTTyXvRrgjSUBjUmkPvNwdQ0lMUlrmOTCgnqWPQAXzI91oI3r-9igKpl9vV8uqBk76P7_3Xy3R-zFgT5jVQVJAruCCr5u6HjEQgTAMwEPkLFd8re4IOX4kL0u-Wd-FrhxdqO3GhHscJsJ9uapRraWGy5CDMBMJX15hNCifmsC_ex7i_r17f4O_JLzJk91fnKIkh2OwLOQeW1p3EQls6_O8wIZ1BGoaXZghCdSCd8lrnP8wHu4JKJ9ti5390XR1wMOfr4aoEbngn-b8OoR0Y-w";
        //mock header to return token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        //create a mock UserDetails object representing the user details associated with the token
        UserDetails userDetails = createUserDetails("user@email.com", "ROLE_USER");
        //create a mock Authentication object representing the authentication associated with the token
        Authentication authentication = createAuthentication(userDetails);
        //mock the jwtTokenProvider to return the authentication when the token is provided
        when(jwtTokenProvider.getAuthentication(token)).thenReturn(authentication);

        // Act
        //This step involves executing the actual method under test
        //invokes doFilterInternal method of JwtTokenFilter object with the mocked HttpServletRequest, HttpServletResponse, and FilterChain
        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        //verify the expected behavior and interactions
        //use verify to check that the getAuthentication method of the JwtTokenProvider is called exactly once with the provided token
        verify(jwtTokenProvider, times(1)).getAuthentication(token);
        verify(request, times(1)).getHeader("Authorization");
        verify(filterChain, times(1)).doFilter(request, response);
    }

    //return unauthorized response when token is invalid
    //verifies that the response status is set to HttpServletResponse.SC_UNAUTHORIZED
    //also verifies that the filter chain is not executed
    @Test
    void doFilterInternal_WithInvalidToken_ShouldReturnUnauthorized() throws ServletException, IOException {
        // Arrange
        String token = "invalid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.getAuthentication(token)).thenThrow(new JwtException("Invalid token"));
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        // Act
        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider).getAuthentication(token);
        verify(request).getHeader("Authorization");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("Invalid JWT token");
        verify(filterChain, never()).doFilter(request, response);

    }

    private Authentication createAuthentication(UserDetails userDetails) {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(authentication.getCredentials()).thenReturn(null);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        return authentication;
    }

    private UserDetails createUserDetails(String email, String role) {
        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        return new User(email, "", Collections.singletonList(authority));
    }

}
