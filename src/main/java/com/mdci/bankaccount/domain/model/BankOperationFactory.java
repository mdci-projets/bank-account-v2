package com.mdci.bankaccount.domain.model;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class BankOperationFactory {

    private final Clock clock;

    public BankOperationFactory(Clock clock) {
        this.clock = clock;
    }

    public BankOperation deposit(Money amount) {
        return new BankOperation(
                UUID.randomUUID().toString(),
                BankOperation.OperationType.DEPOSIT,
                amount,
                LocalDateTime.now(clock)
        );
    }

    public BankOperation withdrawal(Money amount) {
        return new BankOperation(
                UUID.randomUUID().toString(),
                BankOperation.OperationType.WITHDRAWAL,
                amount,
                LocalDateTime.now(clock)
        );
    }
}
