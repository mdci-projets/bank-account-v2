package com.mdci.bankaccount.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdci.bankaccount.application.dto.BankAccountResponseDTO;
import com.mdci.bankaccount.integration.util.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
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

    @BeforeEach
    void resetDatabase() {
        cleanup.clear();
    }

    @Test
    void shouldCreateAndRetrieveAccount() throws Exception {
        // Création
        String json = mockMvc.perform(post("/api/account"))
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
}

