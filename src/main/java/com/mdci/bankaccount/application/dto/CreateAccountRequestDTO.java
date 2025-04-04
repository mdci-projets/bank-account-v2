package com.mdci.bankaccount.application.dto;

import com.mdci.bankaccount.application.validation.ValidAccountCreation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

@ValidAccountCreation
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
    }

    // Factory pour les anciens tests
    public static CreateAccountRequestDTO of(BigDecimal initialBalance, BigDecimal authorizedOverdraft) {
        return new CreateAccountRequestDTO(initialBalance, authorizedOverdraft, "COMPTE_COURANT");
    }
}
