package com.mdci.bankaccount.rest.controller;


import com.mdci.bankaccount.application.dto.AccountStatementDTO;
import com.mdci.bankaccount.application.mapper.StatementMapper;
import com.mdci.bankaccount.application.port.out.DocumentGenerator;
import com.mdci.bankaccount.application.service.BankAccountStatementService;
import com.mdci.bankaccount.domain.model.AccountStatement;
import com.mdci.bankaccount.domain.model.AccountType;
import com.mdci.bankaccount.infrastructure.rest.controller.BankAccountStatementController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankAccountStatementController.class)
class BankAccountStatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankAccountStatementService statementService;

    @Autowired
    private StatementMapper statementMapper;

    @Autowired
    private DocumentGenerator<AccountStatementDTO> documentGenerator;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public BankAccountStatementService statementService() {
            return mock(BankAccountStatementService.class);
        }

        @Bean
        public StatementMapper statementMapper() {
            return mock(StatementMapper.class);
        }

        @Bean
        public DocumentGenerator<AccountStatementDTO> pdfGenerator() {
            return mock(DocumentGenerator.class);
        }
    }

    @Test
    void should_export_statement_as_pdf() throws Exception {
        String accountId = "ACC123";

        AccountStatementDTO dto = new AccountStatementDTO(
                accountId,
                AccountType.COMPTE_COURANT,
                new BigDecimal("840.00"),
                LocalDateTime.of(2025, 4, 5, 10, 0),
                LocalDateTime.of(2025, 3, 5, 0, 0),
                LocalDateTime.of(2025, 4, 5, 0, 0),
                List.of() // on s'en fiche ici
        );

        when(statementService.generateStatementForPeriod(eq(accountId), any(), any()))
                .thenReturn(mock(AccountStatement.class));
        when(statementMapper.toDto(any())).thenReturn(dto);
        when(documentGenerator.generate(any())).thenReturn("fake-pdf-content".getBytes());

        mockMvc.perform(get("/api/account/{id}/statement/export/pdf", accountId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/pdf"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("releve-ACC123.pdf")))
                .andExpect(content().bytes("fake-pdf-content".getBytes()));
    }
}


