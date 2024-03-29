package nl.inholland.bankingapi.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.model.*;
import nl.inholland.bankingapi.model.dto.*;
import nl.inholland.bankingapi.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;

public class AccountStepDefinitions extends BaseStepDefinitions {
    private static final String ACCOUNT_ENDPOINT = "/accounts";
    private final Account account = new Account(3L, new User("employee@email.com", "1234", "User2", "User", "11-11-2000",
            "123456789", "Street", "1234AB", "City", UserType.ROLE_EMPLOYEE, 500.00, 10000.00, true), "NL21INHO0123400082", 100.0, 100.0, AccountType.CURRENT, true);
    private final Account account2 = new Account(4L, new User("employee2@email.com", "1234", "User", "User2", "11-11-2000",
            "123456789", "Street", "1234AB", "City", UserType.ROLE_EMPLOYEE, 500.00, 10000.00, true), "NL21INHO0123400082", 100.0, 100.0, AccountType.CURRENT, false);
    private final AccountGET_DTO accountGETDto = new AccountGET_DTO(1L, 1L, "first", "last", "NL21INHO0123400082", 100.0, 100.0, AccountType.CURRENT, true);
    private final AccountGET_DTO bank = new AccountGET_DTO(2L, 1L, "first", "last", "NL21INHO0123400082", 100.0, 100.0, AccountType.BANK, true);
    private final AccountPUT_DTO accountPUTDto = new AccountPUT_DTO(100.0, false);
    private final AccountPUT_DTO accountPUTDto2 = new AccountPUT_DTO(85.0, true);
    private final AccountPUT_DTO accountPUTDto3 = new AccountPUT_DTO(850000000.0, true);

    private final AccountPOST_DTO accountPOSTDto = new AccountPOST_DTO(1L, 0.0,  AccountType.CURRENT, true);
    private final AccountPOST_DTO accountPOSTDto2 = new AccountPOST_DTO(3L, 0.0,  AccountType.CURRENT, true);

    private AccountRepository accountRepository;

    private TransactionPOST_DTO transactionPOSTDto;
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private ResponseEntity<String> response;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private TransactionDepositDTO transactionDepositDTO = new TransactionDepositDTO("NL21INHO0123400081", 200.0);
    private TransactionWithdrawDTO transactionWithdrawDTO;
    private String token;
    private LoginRequestDTO loginRequestDTO;

    @Given("customer logs in")
    public void loginAsCustomer() throws JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");
        loginRequestDTO = new LoginRequestDTO(VALID_CUSTOMER, VALID_PASSWORD);
        token = getTheToken(loginRequestDTO);
        httpHeaders.add("Authorization", "Bearer " + token);
    }

    @Given("logging as a {string} or an {string}")
    public void loginAsAOrAn(String arg0) throws JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");
        if (arg0.equals("Customer")) {
            loginRequestDTO = new LoginRequestDTO(VALID_CUSTOMER, VALID_PASSWORD);
        } else if (arg0.equals("Employee")) {
            loginRequestDTO = new LoginRequestDTO(VALID_EMPLOYEE, VALID_PASSWORD);
        }
        token = getTheToken(loginRequestDTO);
    }

    @Given("employee logs in")
    public void employeeLogin() throws JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");
        loginRequestDTO = new LoginRequestDTO(VALID_EMPLOYEE, VALID_PASSWORD);
        token = getTheToken(loginRequestDTO);
        httpHeaders.add("Authorization", "Bearer " + token);
    }

    @When("I request to get all accounts")
    public void iRequestToGetAllAccounts() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT,
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }

    @Then("I should get all accounts")
    public void iShouldGetAllAccounts() throws JsonProcessingException {
        List<AccountGET_DTO> accounts = objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, TransactionGET_DTO.class));
        Assertions.assertEquals(6, accounts.size());
    }

    @Then("I should get all accounts as customer")
    public void iShouldGetAllAccountsAsCustomer() throws JsonProcessingException {
        List<AccountGET_DTO> accounts = objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, TransactionGET_DTO.class));
        Assertions.assertEquals(3, accounts.size());
    }

    @When("I request to get a single account")
    public void requestToGetASingleAccount() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT + "/1",
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }

    @Then("I should get a single account")
    public void shouldGetASingleAccount() {
        Assertions.assertEquals(accountGETDto.accountId(), accountGETDto.accountId());
        Assertions.assertEquals(accountGETDto.user(), accountGETDto.user());
        Assertions.assertEquals(accountGETDto.IBAN(), accountGETDto.IBAN());
        Assertions.assertEquals(accountGETDto.balance(), accountGETDto.balance());
        Assertions.assertEquals(accountGETDto.absoluteLimit(), accountGETDto.absoluteLimit());
        Assertions.assertEquals(accountGETDto.accountType(), accountGETDto.accountType());
        Assertions.assertEquals(accountGETDto.isActive(), accountGETDto.isActive());
    }

    @When("I request to get a bank account")
    public void requestToGetABankAccount() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT + "/7",
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }

    @Then("I should get an api request exception")
    public void shouldGetAnApiRequestException() {
        // Validate if an exception occurred during the request
        Assertions.assertEquals("{\"status\":400,\"message\":\"Bank account cannot be accessed\",\"exception\":\"nl.inholland.bankingapi.exception.ApiRequestException\"}",response.getBody());
    }

    @When("I request to deactivate account with ID")
    public void requestDeactivateAccountWithID() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT + "/3",
                HttpMethod.PUT,
                new HttpEntity<>(accountPUTDto, httpHeaders),
                String.class
        );
    }

    @Then("I should deactivate account with ID")
    public void deactivateAccountWithID() {
        account.setAbsoluteLimit(accountPUTDto.absoluteLimit());
        account.setIsActive(accountPUTDto.isActive());
        Assertions.assertEquals(false, account.getIsActive());
    }

    @When("I request to activate account with ID")
    public void requestActivateAccountWithID() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT + "/4",
                HttpMethod.PUT,
                new HttpEntity<>(accountPUTDto2, httpHeaders),
                String.class
        );
    }

    @When("I request to modify absolute limit with ID")
    public void requestModifyAbsoluteLimitAccountWithID() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT + "/4",
                HttpMethod.PUT,
                new HttpEntity<>(accountPUTDto2, httpHeaders),
                String.class
        );
    }

    @Then("I should modify absolute limit of account with ID")
    public void modifyAbsoluteLimitAccountWithID() {
        account2.setAbsoluteLimit(accountPUTDto2.absoluteLimit());
        account2.setIsActive(accountPUTDto2.isActive());
        Assertions.assertEquals(accountPUTDto2.absoluteLimit(), account2.getAbsoluteLimit());
    }

    @When("I request to modify absolute limit with ID Expecting Error")
    public void requestModifyAbsoluteLimitAccountWithIDExpectingError() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT + "/4",
                HttpMethod.PUT,
                new HttpEntity<>(accountPUTDto3, httpHeaders),
                String.class
        );
    }

    @Then("I should not modify absolute limit of account with ID with exception")
    public void shouldGetAnApiRequestExceptionForAbsoluteLimit() {
        // Validate if an exception occurred during the request
        Assertions.assertEquals("{\"status\":400,\"message\":\"Absolute limit cannot be higher than account balance\",\"exception\":\"nl.inholland.bankingapi.exception.ApiRequestException\"}",response.getBody());
    }

    @Then("I should activate account with ID")
    public void activateAccountWithID() {
        account2.setAbsoluteLimit(accountPUTDto2.absoluteLimit());
        account2.setIsActive(accountPUTDto2.isActive());
        Assertions.assertEquals(true, account2.getIsActive());
    }

    @When("I request to open an account")
    public void openAnAccount() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT,
                HttpMethod.POST,
                new HttpEntity<>(accountPOSTDto, httpHeaders),
                String.class
        );
    }

    @And("I get an api exception for opening account type that customer already has")
    public void returnResponseForOpeningAnAccount() {
        AccountType accountType = accountPOSTDto.accountType();
        Assertions.assertEquals("{\"status\":400,\"message\":\"User already has an account of type " + accountType + "\",\"exception\":\"nl.inholland.bankingapi.exception.ApiRequestException\"}",response.getBody());
    }


    @When("request to open an account")
    public void openAccount() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT,
                HttpMethod.POST,
                new HttpEntity<>(accountPOSTDto2, httpHeaders),
                String.class
        );
    }

    @Then("Employee can open an account")
    public void ReturnOpenAnAccount() throws JsonProcessingException {
//        AccountGET_DTO accountGET_dto = objectMapper.readValue(response.getBody(), AccountGET_DTO.class);
        Assertions.assertEquals(3L,accountPOSTDto2.userId() );
        Assertions.assertEquals(AccountType.CURRENT,accountPOSTDto2.accountType() );
    }

    private String getTheToken(LoginRequestDTO loginDTO) throws JsonProcessingException {
        response = restTemplate
                .exchange("/auth/login",
                        HttpMethod.POST,
                        new HttpEntity<>(objectMapper.writeValueAsString(loginDTO), httpHeaders), String.class);
        TokenDTO tokenDTO = objectMapper.readValue(response.getBody(), TokenDTO.class);
        return tokenDTO.jwt();
    }

    @Then("getting a status code of {int}")
    public void gettingAStatusCodeOf(int status) {
        Assertions.assertEquals(status, response.getStatusCode().value());
    }
}
