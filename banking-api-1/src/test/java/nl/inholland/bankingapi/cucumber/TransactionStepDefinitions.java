package nl.inholland.bankingapi.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.dto.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Log
public class TransactionStepDefinitions extends BaseStepDefinitions {
    private static final String TRANSACTION_ENDPOINT = "/transactions";
    private final TransactionGET_DTO transactionGET_dto = new TransactionGET_DTO(1, "NL21INHO0123400081", "NL21INHO0123400082", 100.0, TransactionType.TRANSFER, LocalDateTime.now().toString().substring(0, 19), 4);

    private TransactionPOST_DTO transactionPOSTDto;
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private ResponseEntity<String> response;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private TransactionDepositDTO transactionDepositDTO = new TransactionDepositDTO("NL21INHO0123400081", 200.0);
    private final TransactionWithdrawDTO transactionWithdrawDTO = new TransactionWithdrawDTO("NL21INHO0123400081", 200.0);
    private String token;
    private LoginRequestDTO loginRequestDTO;


    @When("I request to get all transactions")
    public void iRequestToGetAllTransactions() {
        response = restTemplate.exchange(
                TRANSACTION_ENDPOINT,
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }

    @Then("I should get all transactions")
    public void iShouldGetAllTransactions() throws JsonProcessingException {
        List<TransactionGET_DTO> transactions;
            transactions = Arrays.asList(objectMapper.readValue(response.getBody(), TransactionGET_DTO[].class));
        Assertions.assertEquals(2, transactions.size());
    }


    //Scenario 2
    @When("I request to get a single transaction")
    public void iRequestToGetASingleTransaction() {
        response = restTemplate.exchange(
                TRANSACTION_ENDPOINT + "/1",
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }

    @Then("I get a status code of {int}")
    public void iGetAStatusCodeOf(int status) {
    Assertions.assertEquals(status, response.getStatusCode().value());
    }

    @Then("I should get a single transaction")
    public void iShouldGetASingleTransaction() {
        Assertions.assertEquals(transactionGET_dto.transactionId(), transactionGET_dto.transactionId());
        Assertions.assertEquals(transactionGET_dto.fromIban(), transactionGET_dto.fromIban());
        Assertions.assertEquals(transactionGET_dto.toIban(), transactionGET_dto.toIban());
        Assertions.assertEquals(transactionGET_dto.amount(), transactionGET_dto.amount());
        Assertions.assertEquals(transactionGET_dto.type(), transactionGET_dto.type());
        Assertions.assertEquals(transactionGET_dto.timeStamp(), transactionGET_dto.timeStamp().substring(0, 19));
        Assertions.assertEquals(transactionGET_dto.performingUserId(), transactionGET_dto.performingUserId());
        Assertions.assertEquals(transactionGET_dto.performingUserId(), transactionGET_dto.performingUserId());
    }

    @Given("I have an invalid token")
    public void iHaveAnInvalidToken() {
        token = INVALID_TOKEN;
        httpHeaders.clear();
        httpHeaders.add("Authorization", "Bearer " + token);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @When("I request to create a transaction")
    public void iRequestToCreateATransaction() {
        response = restTemplate.exchange(
                TRANSACTION_ENDPOINT,
                HttpMethod.POST,
                new HttpEntity<>(
                        transactionPOSTDto,
                        httpHeaders),
                String.class);
    }

    @When("I request to deposit to selected account")
    public void iRequestToDepositToSelectedAccount() {
        transactionDepositDTO = new TransactionDepositDTO("NL21INHO0123400081", 200.0);
        response = restTemplate.exchange(
                TRANSACTION_ENDPOINT + "/deposit",
                HttpMethod.POST,
                new HttpEntity<>(
                        transactionPOSTDto,
                        httpHeaders),
                String.class);
    }

    @Given("I have a valid token for role {string} or role {string}")
    public void iHaveAValidTokenForRoleCustomerOrRoleEmployee(String roleCustomer, String roleEmployee) {
        if (roleCustomer.equals("Customer"))
            setHttpHeaders(CUSTOMER_TOKEN);
        else if (roleEmployee.equals("Employee"))
            setHttpHeaders(EMPLOYEE_TOKEN);
    }

    @Given("I have a valid token for role {string}")
    public void iHaveAValidTokenForRole(String role) {
        if (role.equals("Customer"))
            setHttpHeaders(CUSTOMER_TOKEN);
        else if (role.equals("Employee"))
            setHttpHeaders(EMPLOYEE_TOKEN);
    }

    //Scenario 4
    @And("I want to withdraw from current account amount {double}")
    public void iWantToWithdrawFromCurrentAccountAmount(double amount) {
        Assertions.assertEquals(transactionWithdrawDTO.amount(), amount);
    }

    @When("I request to withdraw from selected account")
    public void iRequestToWithdrawFromSelectedAccount() {
        response = restTemplate.exchange(
                TRANSACTION_ENDPOINT + "/withdraw",
                HttpMethod.POST,
                new HttpEntity<>(
                        transactionWithdrawDTO,
                        httpHeaders),
                String.class);
    }

    private String getToken(LoginRequestDTO loginDTO) throws JsonProcessingException {
        response = restTemplate
                .exchange("/auth/login",
                        HttpMethod.POST,
                        new HttpEntity<>(objectMapper.writeValueAsString(loginDTO), httpHeaders), String.class);
        TokenDTO tokenDTO = objectMapper.readValue(response.getBody(), TokenDTO.class);
        return tokenDTO.jwt();
    }

    @Given("I login as a customer")
    public void iLoginAsACustomer() throws JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");
        loginRequestDTO = new LoginRequestDTO(VALID_CUSTOMER, VALID_PASSWORD);
        token = getToken(loginRequestDTO);
    }

    @Given("I login as a {string} or an {string}")
    public void iLoginAsAOrAn(String arg0, String arg1) throws JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");
        if (arg0.equals("Customer")) {
            loginRequestDTO = new LoginRequestDTO(VALID_CUSTOMER, VALID_PASSWORD);
        } else if (arg1.equals("Employee")) {
            loginRequestDTO = new LoginRequestDTO(VALID_EMPLOYEE, VALID_PASSWORD);
        }
        token = getToken(loginRequestDTO);
    }

    @Given("I login as an {string}")
    public void iLoginAsAn(String arg0) throws JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");
        if (arg0.equals("Employee")) {
            loginRequestDTO = new LoginRequestDTO(VALID_EMPLOYEE, VALID_PASSWORD);
        }
        token = getToken(loginRequestDTO);
    }


    //Scenario 3

}
