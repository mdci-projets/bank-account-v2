package com.mdci.bankaccount.application.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BankOperationRequestDTO(
        @NotNull(message = "{bank.operation.amount.notnull}")
        @Positive(message = "{bank.operation.amount.positive}")
        BigDecimal amount
) {
}
