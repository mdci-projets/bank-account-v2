package com.mdci.bankaccount.application.dto;


import java.math.BigDecimal;
import com.mdci.bankaccount.domain.model.AccountType;

public record BankAccountResponseDTO(
        String id,
        BigDecimal balance,
        AccountType accountType,
        BigDecimal authorizedOverdraft,
        BigDecimal depositCeiling
) {
}
