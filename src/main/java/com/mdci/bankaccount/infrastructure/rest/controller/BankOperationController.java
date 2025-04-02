package com.mdci.bankaccount.infrastructure.rest.controller;

import com.mdci.bankaccount.application.dto.BankOperationRequestDTO;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.domain.port.in.IBankOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/operations")
@Validated
@Tag(name = "Opérations", description = "Dépôts, retraits et consultation de solde")
public class BankOperationController {

    private final IBankOperationService operationService;

    public BankOperationController(IBankOperationService operationService) {
        this.operationService = operationService;
    }

    @Operation(summary = "Effectuer un dépôt sur un compte", responses = {
            @ApiResponse(responseCode = "200", description = "Dépôt effectué avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable String accountId,
            @Valid @RequestBody BankOperationRequestDTO request) {
        operationService.deposit(accountId, new Money(request.amount()));
        return ResponseEntity.ok("Dépôt effectué avec succès");
    }

    @Operation(summary = "Effectuer un retrait depuis un compte", responses = {
            @ApiResponse(responseCode = "200", description = "Retrait effectué avec succès"),
            @ApiResponse(responseCode = "400", description = "Solde insuffisant ou requête invalide")
    })
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<String> withdraw(
            @PathVariable String accountId,
            @Valid @RequestBody BankOperationRequestDTO request) {
        operationService.withdraw(accountId, new Money(request.amount()));
        return ResponseEntity.ok("Retrait effectué avec succès");
    }

    @Operation(summary = "Obtenir le solde d’un compte à une date donnée", responses = {
            @ApiResponse(responseCode = "200", description = "Solde récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalanceAtDate(
            @PathVariable String accountId,
            @RequestParam("date") @Parameter(description = "Date au format yyyy-MM-dd", example = "2025-01-15") LocalDate date) {
        return ResponseEntity.ok(operationService.getBalanceAtDate(accountId, date));
    }
}
