package com.mdci.bankaccount.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdci.bankaccount.application.dto.BankAccountResponseDTO;
import com.mdci.bankaccount.application.dto.CreateAccountRequestDTO;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.integration.util.DatabaseCleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BankAccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DatabaseCleanup cleanup;

    @Autowired
    private IBankAccountRepository repository;

    @BeforeEach
    void resetDatabase() {
        cleanup.clear();
    }

    @Test
    void shouldCreateAndRetrieveAccount() throws Exception {
        // Création
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(
                new BigDecimal("0"),
                new BigDecimal("0")
        );
        String json = mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.balance", is(0)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BankAccountResponseDTO dto = objectMapper.readValue(json, BankAccountResponseDTO.class);

        // Récupération
        mockMvc.perform(get("/api/account/" + dto.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.id())))
                .andExpect(jsonPath("$.balance", is(0)));
    }

    @Test
    void should_create_account_and_extract_account_number() throws Exception {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(
                new BigDecimal("100"),
                new BigDecimal("50")
        );
        MvcResult result = mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(responseBody);
        String accountNumber = json.get("id").asText();

        // Then
        assertThat(accountNumber).isNotBlank();
        Optional<BankAccount> account = repository.findById(accountNumber);
        Assertions.assertTrue(account.isPresent());
        assertThat(account.get().getAuthorizedOverdraft().amount())
                .isEqualByComparingTo("50.00");
    }

    @Test
    void should_return_bad_request_for_negative_initial_balance() throws Exception {
        mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "initialBalance": -100,
                                      "authorizedOverdraft": 0
                                    }
                                """))
                .andExpect(status().isBadRequest());
    }
}

