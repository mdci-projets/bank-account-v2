package com.mdci.bankaccount.application.dto;


import java.math.BigDecimal;

public record BankAccountResponseDTO(
        String id,
        BigDecimal balance
) {
}
