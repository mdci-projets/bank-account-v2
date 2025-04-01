package com.mdci.bankaccount.infrastructure.rest.controller;

import com.mdci.bankaccount.application.dto.BankOperationRequestDTO;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.domain.port.in.IBankOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/operations")
public class BankOperationController {

    private final IBankOperationService operationService;

    public BankOperationController(IBankOperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Void> deposit(
            @PathVariable String accountId,
            @RequestBody BankOperationRequestDTO request) {
        operationService.deposit(accountId, new Money(request.amount()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<Void> withdraw(
            @PathVariable String accountId,
            @RequestBody BankOperationRequestDTO request) {
        operationService.withdraw(accountId, new Money(request.amount()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalanceAtDate(
            @PathVariable String accountId,
            @RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(operationService.getBalanceAtDate(accountId, date));
    }
}

