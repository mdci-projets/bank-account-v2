package com.mdci.bankaccount.domain.port.in;

import com.mdci.bankaccount.domain.model.AccountStatement;

import java.time.LocalDateTime;

public interface IBankAccountStatementService {
    AccountStatement generateStatementForPeriod(String accountId, LocalDateTime from, LocalDateTime to);
}
