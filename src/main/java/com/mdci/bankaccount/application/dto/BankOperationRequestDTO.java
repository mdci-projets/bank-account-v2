package com.mdci.bankaccount.application.dto;

import java.math.BigDecimal;

public record BankOperationRequestDTO(
        BigDecimal amount
) {
}
