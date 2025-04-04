package com.mdci.bankaccount.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreateAccountRequestDTO(
        @DecimalMin(value = "0.0", inclusive = true, message = "{bank.operation.balance.positive}")
        BigDecimal initialBalance,
        @DecimalMin(value = "0.0", inclusive = true, message = "{bank.operation.authorizedOverdraft.positive}")
        BigDecimal authorizedOverdraft,
        @Pattern(regexp = "COMPTE_COURANT|LIVRET", message = "{bank.account.type.invalid}")
        String accountType
) {
    public CreateAccountRequestDTO {
        if (authorizedOverdraft == null) {
            authorizedOverdraft = BigDecimal.ZERO;
        }

        if ("LIVRET".equals(accountType) && authorizedOverdraft.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Un livret ne peut pas avoir de découvert autorisé.");
        }
    }

    // Factory pour les anciens tests
    public static CreateAccountRequestDTO of(BigDecimal initialBalance, BigDecimal authorizedOverdraft) {
        return new CreateAccountRequestDTO(initialBalance, authorizedOverdraft, "COMPTE_COURANT");
    }
}
