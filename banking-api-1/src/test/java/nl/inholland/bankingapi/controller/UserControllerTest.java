package nl.inholland.bankingapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.inholland.bankingapi.controller.TransactionController;
import nl.inholland.bankingapi.controller.UserController;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.UserType;
import nl.inholland.bankingapi.model.dto.AccountGET_DTO;
import nl.inholland.bankingapi.model.dto.UserGET_DTO;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.UserPOST_DTO;
import nl.inholland.bankingapi.service.UserService;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class})
@EnableMethodSecurity(prePostEnabled = true)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "employee@email.com", password = "1234", roles = "EMPLOYEE")
    void getAllUsers() throws Exception {
        List<UserGET_DTO> users = List.of(
                new UserGET_DTO(1L, "test1@example.com", "John", "Doe", "1990-01-01", "12345", "123 Street", "City", "123456789", UserType.ROLE_CUSTOMER, true, 5000.0, 100.0),
                new UserGET_DTO(2L, "test2@example.com", "Jane", "Smith", "1992-03-15", "54321", "321 Street", "Town", "098765432", UserType.ROLE_CUSTOMER, true, 3000.0, 200.0)
        );

        when(userService.getAllUsers(null, null, null, null, null, null, null, null, null, null, null, null))
                .thenReturn(users);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(users.size())));

        for (int i = 0; i < users.size(); i++) {
            UserGET_DTO user = users.get(i);
            int index = i;

            this.mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].userId", is(i+1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].email", is(user.email())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].firstName", is(user.firstName())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].lastName", is(user.lastName())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].birthDate", is(user.birthDate())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].phoneNumber", is(user.phoneNumber())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].address", is(user.address())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].city", is(user.city())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].userType", is(user.userType().toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].hasAccount", is(user.hasAccount())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].dailyLimit", is(user.dailyLimit())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + index + "].transactionLimit", is(user.transactionLimit())));
        }
    }

    @Test
    void getByIdWithUnauthorisedUserShouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "employee@email.com", password = "1234", roles = "EMPLOYEE")
    void deleteUserShouldSucceedForExistingUserWithActiveAccount() throws Exception {
        // Mock the service
        doNothing().when(userService).deleteUserById(1L);
        // Perform the delete request
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "employee@email.com", password = "1234", roles = "EMPLOYEE")
    void getUserByIdShouldReturnUser() throws Exception {
        long userId = 1L;
        User user = new User("test@example.com", "1234", "Doe", "1990-01-01", "12345", "123 Street", "City", "City", "123456789",UserType.ROLE_CUSTOMER, 100.0, 5000.0, true);
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId));
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId))
                    .andExpect(status().isOk())
                    .andReturn();

            // Convert the response to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = result.getResponse().getContentAsString();
            User responseUser = objectMapper.readValue(jsonResponse, User.class);

            Assert.assertEquals(user.getId(), responseUser.getId());
            Assert.assertEquals(user.getEmail(), responseUser.getEmail());
            Assert.assertEquals(user.getFirstName(), responseUser.getFirstName());
            Assert.assertEquals(user.getLastName(), responseUser.getLastName());
            Assert.assertEquals(user.getBirthDate(), responseUser.getBirthDate());
            Assert.assertEquals(user.getPostalCode(), responseUser.getPostalCode());
            Assert.assertEquals(user.getAddress(), responseUser.getAddress());
            Assert.assertEquals(user.getCity(), responseUser.getCity());
            Assert.assertEquals(user.getPhoneNumber(), responseUser.getPhoneNumber());
            Assert.assertEquals(user.getUserType(), responseUser.getUserType());
            Assert.assertEquals(user.getHasAccount(), responseUser.getHasAccount());
            Assert.assertEquals(user.getDailyLimit(), responseUser.getDailyLimit());
            Assert.assertEquals(user.getTransactionLimit(), responseUser.getTransactionLimit());
    }
}