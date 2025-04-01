package com.mdci.bankaccount.application.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BankOperationRequestDTO(
        @NotNull(message = "Le montant est obligatoire.")
        @Positive(message = "Le montant doit être strictement positif.")
        BigDecimal amount
) {
}
