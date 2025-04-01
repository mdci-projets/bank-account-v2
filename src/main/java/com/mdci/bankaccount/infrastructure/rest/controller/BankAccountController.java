package com.mdci.bankaccount.infrastructure.rest.controller;

import com.mdci.bankaccount.application.dto.BankAccountResponseDTO;
import com.mdci.bankaccount.application.mapper.BankAccountMapper;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.port.in.IBankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class BankAccountController {

    private final IBankAccountService accountService;
    private final BankAccountMapper mapper;

    public BankAccountController(IBankAccountService accountService, BankAccountMapper mapper) {
        this.accountService = accountService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponseDTO> createAccount() {
        BankAccount account = accountService.createAccount();
        return ResponseEntity.ok(mapper.toResponseDTO(account));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccountResponseDTO> getAccount(@PathVariable String id) {
        BankAccount account = accountService.getAccount(id);
        return ResponseEntity.ok(mapper.toResponseDTO(account));
    }
}
