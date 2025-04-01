package com.mdci.bankaccount.domain.port.in;

import com.mdci.bankaccount.domain.model.Money;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IBankOperationService {
    void deposit(String accountId, Money amount);

    void withdraw(String accountId, Money amount);

    BigDecimal getBalanceAtDate(String accountId, LocalDate date);
}
