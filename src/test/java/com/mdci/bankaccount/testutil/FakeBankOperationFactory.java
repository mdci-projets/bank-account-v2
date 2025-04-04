package com.mdci.bankaccount.testutil;

import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.domain.model.BankOperationFactory;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class FakeBankOperationFactory implements BankOperationFactory {

    private final Clock clock;

    public FakeBankOperationFactory(Clock clock) {
        this.clock = clock;
    }

    @Override
    public BankOperation deposit(Money amount) {
        return new BankOperation(
                UUID.randomUUID().toString(),
                BankOperation.OperationType.DEPOSIT,
                amount,
                LocalDateTime.now(clock)
        );
    }

    @Override
    public BankOperation withdrawal(Money amount) {
        return new BankOperation(
                UUID.randomUUID().toString(),
                BankOperation.OperationType.WITHDRAWAL,
                amount,
                LocalDateTime.now(clock)
        );
    }
}
