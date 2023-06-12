//package nl.inholland.bankingapi.cucumber;
//
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;
//import nl.inholland.bankingapi.jwt.JwtKeyProvider;
//import nl.inholland.bankingapi.jwt.JwtTokenProvider;
//import nl.inholland.bankingapi.model.User;
//import nl.inholland.bankingapi.model.UserType;
//import nl.inholland.bankingapi.model.dto.LoginRequestDTO;
//import nl.inholland.bankingapi.model.dto.LoginResponseDTO;
//import nl.inholland.bankingapi.repository.UserRepository;
//import nl.inholland.bankingapi.service.UserDetailsServiceImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.Assert.assertFalse;
//
//public class LoginSteps extends BaseStepDefinitions{
//    private LoginRequestDTO loginRequestDTO;
//    private LoginResponseDTO loginResponseDTO;
//    private JwtKeyProvider jwtKeyProvider = new JwtKeyProvider();
//    private UserRepository userRepository = new UserRepository() {
//        @Override
//        public <S extends User> S save(S entity) {
//            return null;
//        }
//
//        @Override
//        public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
//            return null;
//        }
//
//        @Override
//        public Optional<User> findById(Long aLong) {
//            return Optional.empty();
//        }
//
//        @Override
//        public boolean existsById(Long aLong) {
//            return false;
//        }
//
//        @Override
//        public Iterable<User> findAll() {
//            return null;
//        }
//
//        @Override
//        public Iterable<User> findAllById(Iterable<Long> longs) {
//            return null;
//        }
//
//        @Override
//        public long count() {
//            return 0;
//        }
//
//        @Override
//        public void deleteById(Long aLong) {
//
//        }
//
//        @Override
//        public void delete(User entity) {
//
//        }
//
//        @Override
//        public void deleteAllById(Iterable<? extends Long> longs) {
//
//        }
//
//        @Override
//        public void deleteAll(Iterable<? extends User> entities) {
//
//        }
//
//        @Override
//        public void deleteAll() {
//
//        }
//
//        @Override
//        public List<User> findAll(Specification<User> specification, Pageable pageable) {
//            return null;
//        }
//
//        @Override
//        public List<User> findUserByAccountsAccountId(long id) {
//            return null;
//        }
//
//        @Override
//        public Optional<User> findUserByEmail(String email) {
//            return Optional.empty();
//        }
//
//        @Override
//        public User findUserById(long id) {
//            return null;
//        }
//
//        @Override
//        public void deleteUserById(long id) {
//
//        }
//
//        @Override
//        public List<User> findAllByHasAccount(boolean hasAccount) {
//            return null;
//        }
//    };
//    private UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);
//    private JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(userDetailsService, jwtKeyProvider);
////    private String token;
//    String email = "user@email.com";
//    String password = "1234";
//
//    @Given("^I have a valid login object with valid user and valid password$")
//    public void givenIHaveValidLoginObject() {
//        // Create a new instance of LoginRequestDTO
//        loginRequestDTO = new LoginRequestDTO(email, password);
//    }
//
//    @When("^I call the application login endpoint$")
//    public void whenICallLoginEndpoint() {
//        // Set the request headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Create the request body
//        String requestBody = "{\"email\": \"" + loginRequestDTO.email() + "\", \"password\": \"" + loginRequestDTO.password() + "\"}";
//
//        // Create the HTTP entity with headers and body
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        // Make the POST request to the login endpoint
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8080/auth/login", requestEntity, String.class);
//
//        // Handle the response as needed
//        // For example, you can retrieve the response body and perform assertions
//        String responseBody = responseEntity.getBody();
//
//    }
//
//    @Then("^I receive a token response$")
//    public void thenIReceiveTokenResponse() {
//        // Assert that the token is not null or empty
//        String token = jwtTokenProvider.createToken(email, UserType.ROLE_USER);
//        // Example code
//        assertNotNull(token);
//        assertFalse(token.isEmpty());
//    }
//
//    @Given("^I have a valid username but invalid password$")
//    public void givenIHaveValidUsernameInvalidPassword() {
//        // Implementation logic for setting up a valid username and invalid password
//    }
//
//    @When("^I call the application login endpoint$")
//    public void whenICallLoginEndpointWithValidEmailButInvalidPassword() {
//        // Set the request headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Create the request body
//        String requestBody = "{\"email\": \"" + loginRequestDTO.email() + "\", \"password\": \"" + loginRequestDTO.password() + "\"}";
//
//        // Create the HTTP entity with headers and body
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        // Make the POST request to the login endpoint
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8080/auth/login", requestEntity, String.class);
//
//        // Handle the response as needed
//        // For example, you can retrieve the response body and perform assertions
//        String responseBody = responseEntity.getBody();
//
//    }
//
//    @Then("^I receive http status 401$")
//    public void thenIReceiveHttpStatus401() {
//        // Implementation logic to assert the HTTP status code 401
//    }
//
//    @Given("^I have an invalid email and valid password$")
//    public void givenIHaveInvalidEmailValidPassword() {
//        // Implementation logic for setting up an invalid email and valid password
//    }
//
//    @When("^I call the application login endpoint$")
//    public void whenICallLoginEndpointWithInvalidEmailButValidPassword() {
//        // Set the request headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Create the request body
//        String requestBody = "{\"email\": \"" + loginRequestDTO.email() + "\", \"password\": \"" + loginRequestDTO.password() + "\"}";
//
//        // Create the HTTP entity with headers and body
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        // Make the POST request to the login endpoint
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8080/auth/login", requestEntity, String.class);
//
//        // Handle the response as needed
//        // For example, you can retrieve the response body and perform assertions
//        String responseBody = responseEntity.getBody();
//
//    }
//
//    @Then("^I receive http status 401$")
//    public void thenIReceiveHttpStatus401Again() {
//        // Implementation logic to assert the HTTP status code 401
//    }
//
//}