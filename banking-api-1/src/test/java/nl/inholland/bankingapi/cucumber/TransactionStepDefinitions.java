package nl.inholland.bankingapi.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.java.Log;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Log
public class TransactionStepDefinitions {
    private static final String TRANSACTION_ENDPOINT = "/transactions";
    private TransactionGET_DTO transactionGET_dto;
    private TransactionPOST_DTO transactionPOSTDto;
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private ResponseEntity<String> response;
    private TestRestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private String token;


    @Given("The endpoint for {string} is avaliable for method {string}")
    public void theEndpointForIsAvaliableForMethod(String endpoint, String method) {

        response = restTemplate.exchange(
                "/" + endpoint,
                HttpMethod.OPTIONS,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
        List<String> options = Arrays.stream(Objects.requireNonNull(response.getHeaders()
                        .get("Allow"))
                .get(0)
                .split(",")).toList();
        Assertions.assertTrue(options.contains(method.toUpperCase()));
    }

    @When("I retrieve all transactions")
    public void iRetrieveAllTransactions() {
        response = restTemplate.exchange(
                TRANSACTION_ENDPOINT,
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }

    @Then("I receive a {int} status code")
    public void iReceiveAStatusCode(int statusCode) {
        Assertions.assertEquals(statusCode, response.getStatusCode().value());
    }

    @And("I get a transaction with id {int}")
    public void iGetATransactionWithId(int id) {
        List<TransactionGET_DTO> transactions = Arrays.asList(objectMapper.convertValue(response.getBody(), TransactionGET_DTO[].class));
        Assertions.assertEquals(id, transactions.get(0).transactionId());
    }

    @When("I retrieve a transaction with id {int}")
    public void iRetrieveATransactionWithId(int id) {
        response = restTemplate.exchange(
                TRANSACTION_ENDPOINT + "/" + id,
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }

    @Given("I have a valid token")
    public void iHaveAValidToken() {
        httpHeaders.clear();
        httpHeaders.add("Authorization", "Bearer " + token);
    }

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
    public void iShouldGetAllTransactions() {
        List<TransactionGET_DTO> transactions = Arrays.asList(objectMapper.convertValue(response.getBody(), TransactionGET_DTO[].class));
        Assertions.assertEquals(2, transactions.size());
    }

    @Then("I get a status code of {int}")
    public void iGetAStatusCodeOf(int statusCode) {
        Assertions.assertEquals(statusCode, response.getStatusCode().value());
    }
}
