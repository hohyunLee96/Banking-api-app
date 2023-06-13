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

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSIsImF1dGgiOiJST0xFX0NVU1RPTUVSIiwiaWF0IjoxNjg2MzAwMTMxLCJleHAiOjE2ODYzMDAxMzF9.BKx5_i-hpC11TEKdPKZS2aopH9pdrcTDKuXhlYUvfnQ9iw2vG0mb-m95ZMrQ4YC3ZYTTyXvRrgjSUBjUmkPvNwdQ0lMUlrmOTCgnqWPQAXzI91oI3r-9igKpl9vV8uqBk76P7_3Xy3R-zFgT5jVQVJAruCCr5u6HjEQgTAMwEPkLFd8re4IOX4kL0u-Wd-FrhxdqO3GhHscJsJ9uapRraWGy5CDMBMJX15hNCifmsC_ex7i_r17f4O_JLzJk91fnKIkh2OwLOQeW1p3EQls6_O8wIZ1BGoaXZghCdSCd8lrnP8wHu4JKJ9ti5390XR1wMOfr4aoEbngn-b8OoR0Y-w";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        UserDetails userDetails = createUserDetails("user@email.com", "ROLE_USER");
        Authentication authentication = createAuthentication(userDetails);
        when(jwtTokenProvider.getAuthentication(token)).thenReturn(authentication);

        // Act
        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider, times(1)).getAuthentication(token);
        verify(request, times(1)).getHeader("Authorization");
        verify(filterChain, times(1)).doFilter(request, response);
    }

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
        verify(jwtTokenProvider, times(1)).getAuthentication(token);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer, times(1)).write("Invalid JWT token");
        verify(filterChain, never()).doFilter(request, response);

    }


    @Test
    void doFilterInternal_WithException_ShouldReturnInternalServerError() throws ServletException, IOException {
        // Arrange
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyQGVtYWlsLmNvbSIsImF1dGgiOiJST0xFX0NVU1RPTUVSIiwiaWF0IjoxNjg2MzAwMTMxLCJleHAiOjE2ODYzMDAxMzF9.BKx5_i-hpC11TEKdPKZS2aopH9pdrcTDKuXhlYUvfnQ9iw2vG0mb-m95ZMrQ4YC3ZYTTyXvRrgjSUBjUmkPvNwdQ0lMUlrmOTCgnqWPQAXzI91oI3r-9igKpl9vV8uqBk76P7_3Xy3R-zFgT5jVQVJAruCCr5u6HjEQgTAMwEPkLFd8re4IOX4kL0u-Wd-FrhxdqO3GhHscJsJ9uapRraWGy5CDMBMJX15hNCifmsC_ex7i_r17f4O_JLzJk91fnKIkh2OwLOQeW1p3EQls6_O8wIZ1BGoaXZghCdSCd8lrnP8wHu4JKJ9ti5390XR1wMOfr4aoEbngn-b8OoR0Y-w";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.getAuthentication(token)).thenThrow(new RuntimeException("Something went wrong"));
        PrintWriter writerMock = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writerMock);

        // Act
        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider, times(1)).getAuthentication(token);
        verify(request, times(1)).getHeader("Authorization");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(writerMock, times(1)).write("Something went wrong");
        verify(filterChain, never()).doFilter(request, response);

    }

    private UserDetails createUserDetails(String username, UserType userType) {
        return User.withUsername(username)
                .password("password")
                .roles(String.valueOf(userType))
                .build();
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
