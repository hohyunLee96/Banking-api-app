package nl.inholland.bankingapi.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.bankingapi.model.dto.LoginRequestDTO;
import nl.inholland.bankingapi.model.dto.LoginResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginStepDefinitions extends BaseStepDefinitions {
    private static final String LOGIN_ENDPOINT = "/auth/login";
    private LoginRequestDTO loginRequestDTO;

    @Autowired
    private ObjectMapper objectMapper;

    //Scenario: Log in with valid email and valid password
    @Given("I have a valid login object with valid email and valid password")
    public void iHaveAValidLoginObjectWithValidEmailAndValidPassword() {
        loginRequestDTO = new LoginRequestDTO(VALID_CUSTOMER, VALID_PASSWORD);
    }

    @When("I call the application login endpoint")
    public void iCallTheApplicationLoginEndpoint() {
        response = restTemplate.postForEntity(
                LOGIN_ENDPOINT,
                loginRequestDTO,
                String.class);
    }

    @Then("I receive a token response")
    public void iReceiveATokenResponse() throws JsonProcessingException {
        LoginResponseDTO tokenResponse = objectMapper.readValue(response.getBody(),
                LoginResponseDTO.class);
        Assertions.assertNotNull(tokenResponse.jwt());
        Assertions.assertNotEquals(0, tokenResponse.id());
    }

    //Scenario: Log in with valid email and invalid password
    @Given("I have a valid login object with valid email and invalid password")
    public void iHaveAValidLoginObjectWithValidEmailAndInvalidPassword() {
        loginRequestDTO = new LoginRequestDTO(VALID_CUSTOMER, INVALID_PASSWORD);
    }

    @Then("I receive a {int} response")
    public void iReceiveAResponse(int arg0) throws JsonProcessingException {
        LoginResponseDTO loginResponse = objectMapper.readValue(response.getBody(),
                LoginResponseDTO.class);
        Assertions.assertNotEquals(401, response.getStatusCodeValue());
        Assertions.assertNull(loginResponse.jwt());
    }

    @And("I receive a message that the password is invalid")
    public void iReceiveAMessageThatThePasswordIsInvalid() {
        response = restTemplate.postForEntity(
                LOGIN_ENDPOINT,
                loginRequestDTO,
                String.class);
        if (response.getStatusCodeValue() == 401) {
            Assertions.assertEquals("Invalid password", response.getBody());
        }
    }

    //Scenario: Log in with invalid email and valid password
    @Given("I have a valid login object with invalid email and valid password")
    public void iHaveAValidLoginObjectWithInvalidEmailAndValidPassword() {
        loginRequestDTO = new LoginRequestDTO(INVALID_EMAIL, VALID_PASSWORD);
    }

    @And("I receive a message that the email is invalid")
    public void iReceiveAMessageThatTheEmailIsInvalid() {
        response = restTemplate.postForEntity(
                LOGIN_ENDPOINT,
                loginRequestDTO,
                String.class);
        if (response.getStatusCodeValue() == 401) {
            Assertions.assertEquals("Invalid email", response.getBody());
        }
    }

    //Scenario: Log in with invalid email and invalid password
    @Given("I have a valid login object with invalid email and invalid password")
    public void iHaveAValidLoginObjectWithInvalidEmailAndInvalidPassword() {
        loginRequestDTO = new LoginRequestDTO(INVALID_EMAIL, INVALID_PASSWORD);
    }

    @And("I receive a message that credentials are invalid")
    public void iReceiveAMessageThatCredentialsAreInvalid() {
        response = restTemplate.postForEntity(
                LOGIN_ENDPOINT,
                loginRequestDTO,
                String.class);
        if (response.getStatusCodeValue() == 401) {
            Assertions.assertEquals("Invalid credentials", response.getBody());
        }
    }

    //Scenario: Log in with valid email and empty password
    @Given("I have a valid login object with valid email and empty password")
    public void iHaveAValidLoginObjectWithValidEmailAndEmptyPassword() {
        loginRequestDTO = new LoginRequestDTO(VALID_CUSTOMER, EMPTY_PASSWORD);
    }

    @And("I receive a message that the password is empty")
    public void iReceiveAMessageThatThePasswordIsEmpty() {
        response = restTemplate.postForEntity(
                LOGIN_ENDPOINT,
                loginRequestDTO,
                String.class);
        if (response.getStatusCodeValue() == 400) {
            Assertions.assertEquals("Password is empty", response.getBody());
        }
    }

    //Scenario: Log in with empty email and valid password
    @Given("I have a valid login object with empty email and valid password")
    public void iHaveAValidLoginObjectWithEmptyEmailAndValidPassword() {
        loginRequestDTO = new LoginRequestDTO(EMPTY_EMAIL, VALID_PASSWORD);
    }

    @And("I receive a message that the email is empty")
    public void iReceiveAMessageThatTheEmailIsEmpty() {
        response = restTemplate.postForEntity(
                LOGIN_ENDPOINT,
                loginRequestDTO,
                String.class);
        if (response.getStatusCodeValue() == 400) {
            Assertions.assertEquals("Email is empty", response.getBody());
        }
    }

    //Scenario: Log in with empty email and empty password
    @Given("I have a valid login object with empty email and empty password")
    public void iHaveAValidLoginObjectWithEmptyEmailAndEmptyPassword() {
        loginRequestDTO = new LoginRequestDTO(EMPTY_EMAIL, EMPTY_PASSWORD);
    }
}