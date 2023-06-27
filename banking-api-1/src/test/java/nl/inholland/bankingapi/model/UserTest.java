package nl.inholland.bankingapi.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "password", "John", "Doe", "1990-01-01", "12345", "123 Street", "City", "1234567890", UserType.ROLE_CUSTOMER, 1000.0, 5000.0, true);
        user.setId(1L);
    }

    @Test
    void testGetters() {
        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("1990-01-01", user.getBirthDate());
        assertEquals("12345", user.getPostalCode());
        assertEquals("123 Street", user.getAddress());
        assertEquals("City", user.getCity());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals(UserType.ROLE_CUSTOMER, user.getUserType());
        assertEquals(true, user.getHasAccount());
        assertEquals(1000.0, user.getDailyLimit());
        assertEquals(5000.0, user.getTransactionLimit());
        assertNotNull(user.getAccounts());
        assertTrue(user.getAccounts().isEmpty());
    }

    @Test
    void testSetters() {
        user.setId(2L);
        assertEquals(2L, user.getId());

        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail());

        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());

        user.setFirstName("Jane");
        assertEquals("Jane", user.getFirstName());

        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());

        user.setBirthDate("1995-01-01");
        assertEquals("1995-01-01", user.getBirthDate());

        user.setPostalCode("54321");
        assertEquals("54321", user.getPostalCode());

        user.setAddress("456 Street");
        assertEquals("456 Street", user.getAddress());

        user.setCity("New City");
        assertEquals("New City", user.getCity());

        user.setPhoneNumber("0987654321");
        assertEquals("0987654321", user.getPhoneNumber());

        user.setUserType(UserType.ROLE_EMPLOYEE);
        assertEquals(UserType.ROLE_EMPLOYEE, user.getUserType());

        user.setHasAccount(false);
        assertEquals(false, user.getHasAccount());

        user.setDailyLimit(2000.0);
        assertEquals(2000.0, user.getDailyLimit());

        user.setTransactionLimit(10000.0);
        assertEquals(10000.0, user.getTransactionLimit());

        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account());
        user.setAccounts(accounts);
        assertEquals(accounts, user.getAccounts());
    }

    @Test
    void testConstructor() {
        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("1990-01-01", user.getBirthDate());
        assertEquals("12345", user.getPostalCode());
        assertEquals("123 Street", user.getAddress());
        assertEquals("City", user.getCity());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals(UserType.ROLE_CUSTOMER, user.getUserType());
        assertEquals(true, user.getHasAccount());
        assertEquals(1000.0, user.getDailyLimit());
        assertEquals(5000.0, user.getTransactionLimit());
        assertNotNull(user.getAccounts());
        assertTrue(user.getAccounts().isEmpty());
    }
}