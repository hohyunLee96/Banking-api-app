package nl.inholland.bankingapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.TransactionGET_DTO;
import nl.inholland.bankingapi.model.dto.TransactionPOST_DTO;
import nl.inholland.bankingapi.model.dto.TransactionWithdrawDTO;
import nl.inholland.bankingapi.service.AccountService;
import nl.inholland.bankingapi.service.TransactionService;
import nl.inholland.bankingapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {TransactionController.class})
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
@EnableMethodSecurity(prePostEnabled = true)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private TransactionGET_DTO transactionGETDto;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private UserService userService;

    private Transaction transaction;
    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.openMocks(this);
        transactionController = new TransactionController(transactionService);
        transactionGETDto = new TransactionGET_DTO(1, "NL21INHO0123400081", "NL21INHO0123400082", 120.0, TransactionType.TRANSFER, LocalDateTime.now().toString(), 123L);
        transaction = new Transaction(1L, accountService.getAccountByIBAN("NL21INHO0123400081"), accountService.getAccountByIBAN("NL21INHO0123400082"), 120.0, LocalDateTime.now(), TransactionType.TRANSFER, new User());
    }

    private String asJsonString(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllTransactions() {
        List<TransactionGET_DTO> transactionGETDtoList = Collections.singletonList(transactionGETDto);
        when(transactionService.getAllTransactions(null, null, null, null, null, null, null, null, null, null)).thenReturn(transactionGETDtoList);
        assertThat(transactionGETDtoList).isNotNull();
    }

    @Test
    @WithMockUser(username = "employee", roles = {"EMPLOYEE"})
    void getTransactionById() throws Exception {
        when(transactionService.getTransactionById(1L)).thenReturn(transactionGETDto);
        MockHttpServletResponse response = mockMvc.perform(get("/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(asJsonString(transactionGETDto));
    }


    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    void addTransaction() throws Exception {
        TransactionPOST_DTO transactionPOSTDto = new TransactionPOST_DTO("NL21INHO0123400081", "NL21INHO0123400082", 120.0, TransactionType.TRANSFER, 123L);
        Transaction transaction = new Transaction(1L, new Account(), new Account(), 120.0, LocalDateTime.now(), TransactionType.TRANSFER, new User());
        when(transactionService.addTransaction(transactionPOSTDto)).thenReturn(transaction);

//        MockHttpServletResponse response = new MockHttpServletResponse();
//        response.setStatus(HttpStatus.CREATED.value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
//        response.getWriter().write(asJsonString(transaction));

        MockHttpServletResponse response = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .content(asJsonString(transaction)))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(asJsonString(transaction));
    }

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    void withdraw() throws Exception {

        TransactionWithdrawDTO transactionWithdrawDto = new TransactionWithdrawDTO("NL21INHO0123400081", 120.0);
        when(transactionService.withdraw(transactionWithdrawDto)).thenReturn(transaction);

        MockHttpServletResponse response = mockMvc.perform(post("/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .content(asJsonString(transaction)))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(asJsonString(transaction));

    }

    @Test
    void deposit() {
    }


}
