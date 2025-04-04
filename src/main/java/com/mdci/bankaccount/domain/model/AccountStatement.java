package com.mdci.bankaccount.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AccountStatement(
        String accountId,
        AccountType accountType,
        BigDecimal currentBalance,
        LocalDateTime issuedAt,
        LocalDateTime from,
        LocalDateTime to,
        List<BankOperation> operations
) {
}
