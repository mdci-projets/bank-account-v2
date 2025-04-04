package com.mdci.bankaccount.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdci.bankaccount.application.dto.BankOperationRequestDTO;
import com.mdci.bankaccount.application.dto.CreateAccountRequestDTO;
import com.mdci.bankaccount.integration.util.DatabaseCleanup;
import com.mdci.bankaccount.integration.util.TestBankOperationFactoryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestBankOperationFactoryConfig.class)
class BankOperationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DatabaseCleanup cleanup;

    @BeforeEach
    void resetDatabase() {
        cleanup.clear();
    }

    @Test
    void shouldDepositMoneyToAccount() throws Exception {
        // Création d’un compte
        CreateAccountRequestDTO requestAccount = CreateAccountRequestDTO.of(
                new BigDecimal("0.00"),
                new BigDecimal("0.00")
        );
        String json = mockMvc.perform(post("/api/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestAccount)))
          .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(json).get("id").asText();

        // Dépôt
        BankOperationRequestDTO request = new BankOperationRequestDTO(BigDecimal.valueOf(200));
        mockMvc.perform(post("/api/operations/" + id + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Vérification du solde
        mockMvc.perform(get("/api/account/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(200.0)));
    }

    @Test
    void shouldReturn404IfAccountNotFound() throws Exception {
        String fakeId = "not-found-123";

        mockMvc.perform(get("/api/account/" + fakeId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Aucun compte trouvé pour l'identifiant")));
    }

    @Test
    void shouldReturn400IfInvalidAmount() throws Exception {
        CreateAccountRequestDTO requestAccount = CreateAccountRequestDTO.of(
                new BigDecimal("0.00"),
                new BigDecimal("0.00")
        );
        String id = objectMapper.readTree(
                mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAccount)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8)
        ).get("id").asText();

        BankOperationRequestDTO request = new BankOperationRequestDTO(BigDecimal.valueOf(-100));

        MvcResult result = mockMvc.perform(post("/api/operations/" + id + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("Réponse = " + responseBody);

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode erreurs = root.path("erreurs");
        assertTrue(erreurs.isArray());
        assertEquals("amount", erreurs.get(0).path("champ").asText());
        assertEquals("Le montant doit être strictement positif.", erreurs.get(0).path("message").asText());
    }

    @Test
    void shouldReturn400IfInsufficientBalance() throws Exception {
        // Création d’un compte
        CreateAccountRequestDTO requestCreateAccount = CreateAccountRequestDTO.of(
                new BigDecimal("0.00"),
                new BigDecimal("0.00")
        );
        String id = objectMapper.readTree(
                mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateAccount)))
                  .andReturn().getResponse().getContentAsString()
        ).get("id").asText();

        BankOperationRequestDTO request = new BankOperationRequestDTO(BigDecimal.valueOf(500));

        mockMvc.perform(post("/api/operations/" + id + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Solde insuffisant")));
    }

    @Test
    void shouldReturn400IfMissingField() throws Exception {
        // Création d’un compte
        CreateAccountRequestDTO request = CreateAccountRequestDTO.of(
                new BigDecimal("0.00"),
                new BigDecimal("0.00")
        );
        String id = objectMapper.readTree(
                mockMvc.perform(post("/api/account")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                        .andReturn().getResponse().getContentAsString()
        ).get("id").asText();

        // Aucun montant
        mockMvc.perform(post("/api/operations/" + id + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Le montant est obligatoire")));
    }

    @Test
    void shouldKeepAccountBalanceInSyncWithOperations() throws Exception {
        // Création d’un compte
        CreateAccountRequestDTO request = CreateAccountRequestDTO.of(
                new BigDecimal("0.00"),
                new BigDecimal("0.00")
        );
        String id = objectMapper.readTree(
                mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString()
        ).get("id").asText();

        // Dépôt de 500 €
        mockMvc.perform(post("/api/operations/" + id + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BankOperationRequestDTO(BigDecimal.valueOf(500)))))
                .andExpect(status().isOk());

        // Retrait de 150 €
        mockMvc.perform(post("/api/operations/" + id + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BankOperationRequestDTO(BigDecimal.valueOf(150)))))
                .andExpect(status().isOk());

        // Solde final attendu : 350 €
        mockMvc.perform(get("/api/account/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(350.0)));
    }

    @Test
    void shouldReturn400IfBalanceAtDateParamIsInvalid() throws Exception {
        // Création d’un compte
        CreateAccountRequestDTO request = CreateAccountRequestDTO.of(
                new BigDecimal("0.00"),
                new BigDecimal("0.00")
        );
        String id = objectMapper.readTree(
                mockMvc.perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                   .andReturn().getResponse().getContentAsString()
        ).get("id").asText();

        // Appel avec une date invalide
        mockMvc.perform(get("/api/operations/" + id + "/balance?date=not-a-date"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("La date fournie est invalide. Format attendu : yyyy-MM-dd (LocalDate).")));
    }


}
