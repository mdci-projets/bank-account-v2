package com.mdci.bankaccount.infrastructure.rest.controller;

import com.mdci.bankaccount.application.dto.BankAccountResponseDTO;
import com.mdci.bankaccount.application.mapper.BankAccountMapper;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.port.in.IBankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@Validated
@Tag(name = "Comptes bancaires", description = "Gestion des comptes")
public class BankAccountController {

    private final IBankAccountService accountService;
    private final BankAccountMapper mapper;

    public BankAccountController(IBankAccountService accountService, BankAccountMapper mapper) {
        this.accountService = accountService;
        this.mapper = mapper;
    }

    @Operation(summary = "Créer un nouveau compte bancaire", responses = {
            @ApiResponse(responseCode = "200", description = "Compte créé avec succès")
    })
    @PostMapping
    public ResponseEntity<BankAccountResponseDTO> createAccount() {
        BankAccount account = accountService.createAccount();
        return ResponseEntity.ok(mapper.toResponseDTO(account));
    }

    @Operation(summary = "Récupérer un compte bancaire par son identifiant", responses = {
            @ApiResponse(responseCode = "200", description = "Compte trouvé"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BankAccountResponseDTO> getAccount(@PathVariable String id) {
        BankAccount account = accountService.getAccount(id);
        return ResponseEntity.ok(mapper.toResponseDTO(account));
    }
}
