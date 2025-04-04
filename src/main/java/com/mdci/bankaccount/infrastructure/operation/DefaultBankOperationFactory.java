package com.mdci.bankaccount.infrastructure.operation;

import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.model.Money;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class DefaultBankOperationFactory implements BankOperationFactory {

    private final Clock clock;

    public DefaultBankOperationFactory(Clock clock) {
        this.clock = clock;
    }

    @Override
    public BankOperation deposit(Money amount) {
        return new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.DEPOSIT, amount, LocalDateTime.now(clock));
    }

    @Override
    public BankOperation withdrawal(Money amount) {
        return new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.WITHDRAWAL, amount, LocalDateTime.now(clock));
    }
}
