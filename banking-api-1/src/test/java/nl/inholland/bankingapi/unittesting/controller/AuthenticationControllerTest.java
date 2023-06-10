package nl.inholland.bankingapi.unittesting.controller;

import nl.inholland.bankingapi.controller.AuthenticationController;
import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.model.dto.LoginRequestDTO;
import nl.inholland.bankingapi.model.dto.LoginResponseDTO;
import nl.inholland.bankingapi.service.AuthenticationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    LoginRequestDTO loginRequestDTO;
    LoginResponseDTO loginResponseDto;
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        loginRequestDTO = new LoginRequestDTO("email@gmail.com", "password");
        loginResponseDto = new LoginResponseDTO("token", "email@gmail.com", 1L);
    }

    @Test
    void login() throws Exception {

        when(authenticationService.login(loginRequestDTO.email(), loginRequestDTO.password()))
                .thenReturn(loginResponseDto);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\": \"" + loginRequestDTO.email() + "\", \"password\": \"" + loginRequestDTO.password() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"jwt\":\"" + loginResponseDto.jwt()
                        + "\",\"id\":" + loginResponseDto.id()
                        + ",\"email\":\"" + loginResponseDto.email()
                        + "\"}"));
        verify(authenticationService).login(loginRequestDTO.email(), loginRequestDTO.password());
    }

    @Test
    void login_THROWS_NOTFOUND_WHEN_URL_IS_WRONG() throws Exception {

        when(authenticationService.login(loginRequestDTO.email(), loginRequestDTO.password()))
                .thenReturn(loginResponseDto);

        mockMvc.perform(post("/wrong/path")
                        .contentType("application/json")
                        .content("{\"email\": \"" + loginRequestDTO.email() + "\", \"password\": \"" + loginRequestDTO.password() + "\"}"))
                .andExpect(status().is(404))
                .andReturn();

    }

    @Test
    void login_THROWS_BADREQUEST_WHEN_PASSWORD_AND_EMAIL_IS_MISSING() throws Exception {

            when(authenticationService.login(loginRequestDTO.email(), loginRequestDTO.password()))
                    .thenReturn(loginResponseDto);

            mockMvc.perform(post("/auth/login")
                            .contentType("application/json")
                            .content(""))
                    .andExpect(status().is(400))
                    .andReturn();

    }

    @Test
    void correct_LOGIN_PRODUCES_A_TOKEN() throws Exception {
        when(authenticationService.login(loginRequestDTO.email(), loginRequestDTO.password()))
                .thenReturn(loginResponseDto);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"email\": \"" + loginRequestDTO.email() + "\", \"password\": \"" + loginRequestDTO.password() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").exists()) // Check if "jwt" key exists in the response
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").isString()) // Check if "jwt" value is a string
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").value(Matchers.not(Matchers.isEmptyOrNullString()))) // Check if "jwt" value is not empty or null
                .andExpect(MockMvcResultMatchers.content().json("{" +
                        "\"jwt\":\"" + loginResponseDto.jwt() + "\",\"id\":" + loginResponseDto.id() + ",\"email\":\"" + loginResponseDto.email() + "\"}"));

        verify(authenticationService).login(loginRequestDTO.email(), loginRequestDTO.password());
    }


}
