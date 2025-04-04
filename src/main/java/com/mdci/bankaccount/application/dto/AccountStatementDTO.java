package com.mdci.bankaccount.application.dto;

import com.mdci.bankaccount.domain.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AccountStatementDTO(
        String accountId,
        AccountType accountType,
        BigDecimal currentBalance,
        LocalDateTime issuedAt,
        LocalDateTime from,
        LocalDateTime to,
        List<OperationDTO> recentOperations
) {
}
