package com.mdci.bankaccount.application.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record CreateAccountRequestDTO(
        @DecimalMin(value = "0.0", inclusive = true, message = "{bank.operation.balance.positive}")
        BigDecimal initialBalance,
        @DecimalMin(value = "0.0", inclusive = true, message = "{bank.operation.authorizedOverdraft.positive}")
        BigDecimal authorizedOverdraft
) {
        public CreateAccountRequestDTO {
                if (authorizedOverdraft == null) {
                        authorizedOverdraft = BigDecimal.ZERO;
                }
        }
}
