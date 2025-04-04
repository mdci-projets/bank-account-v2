package com.mdci.bankaccount.infrastructure.rest.controller;

import com.mdci.bankaccount.application.dto.AccountStatementDTO;
import com.mdci.bankaccount.application.mapper.StatementMapper;
import com.mdci.bankaccount.domain.model.AccountStatement;
import com.mdci.bankaccount.domain.port.in.IBankAccountStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Relevés de compte", description = "Endpoints liés aux relevés de compte")
@RestController
@RequestMapping("/accounts/{id}/statement")
public class BankAccountStatementController {

    private final IBankAccountStatementService statementService;
    private final StatementMapper statementMapper;

    public BankAccountStatementController(IBankAccountStatementService statementService, StatementMapper statementMapper) {
        this.statementService = statementService;
        this.statementMapper = statementMapper;
    }

    @Operation(
            summary = "Obtenir un relevé de compte (JSON)",
            description = "Retourne un relevé avec solde et liste d'opérations sur la période fournie. Dernier mois par défaut.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Relevé JSON", content = @Content(schema = @Schema(implementation = AccountStatementDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Compte introuvable"),
                    @ApiResponse(responseCode = "400", description = "Période invalide")
            }
    )
    @GetMapping
    public AccountStatementDTO getStatement(
            @PathVariable String id,
            @Parameter(description = "Date de début (optionnelle)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @Parameter(description = "Date de fin (optionnelle)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        AccountStatement statement = statementService.generateStatementForPeriod(id, from, to);
        return statementMapper.toDto(statement);
    }
}