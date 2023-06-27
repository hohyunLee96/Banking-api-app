package nl.inholland.bankingapi.service;


import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.UserGET_DTO;
import nl.inholland.bankingapi.model.specifications.UserSpecifications;
import nl.inholland.bankingapi.repository.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static javax.management.Query.eq;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

    }
    @Test
    public void newUserShouldNotBeNull() {
        Assertions.assertNotNull(new User());
    }
    @Test
    public void testGetUserById_NonExistentId_ReturnsUserDTO() {
        //create mock user with a non-existent id
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(123L);
        when(userRepository.findById(123L)).thenReturn(java.util.Optional.of(mockUser));

        //Perform the operation and assert the expected result
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(123L));
    }

    @Test
    public void testGetUserById_ExistingId(){
        //create mock user with an existing id
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));

        //Perform the operation and assert the expected result
        Assertions.assertEquals(mockUser, userService.getUserById(1L).getId());
    }

    @Test
    public void testRegisterUser_ExistingEmail(){
        // Create user1 with an existing email and save it to the database
        User user1 = new User("user@email.com", bCryptPasswordEncoder.encode("1234"), "User1", "User", "11-11-2000",
                "123456789", "Street", "1234AB", "City", UserType.ROLE_CUSTOMER, 1000.00, 1000.00, true);
        userRepository.save(user1);

        // Attempt to save user2 with the same email to the database
        User user2 = new User("user@email.com", bCryptPasswordEncoder.encode("5678"), "User2", "User", "22-22-2000",
                "987654321", "Avenue", "5678CD", "Town", UserType.ROLE_CUSTOMER, 2000.00, 2000.00, true);

        // Assert that an exception is thrown when saving user2
        assertThrows(ApiRequestException.class, () -> userRepository.save(user2));
    }

    @Test
    public void testDeleteUser_HasAccount_ThrowsApiRequestException() {
        // Create a user with hasAccount set to true
        User user = new User();
        user.setHasAccount(true);

        // Mock the userRepository.findById method to return the user
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Perform the operation and assert the expected exception
        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> userService.deleteUserById(1L));
        Assertions.assertEquals("Cannot delete user with an active account", exception.getMessage());
    }
}
