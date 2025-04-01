package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankAccount {
    private final String id;
    private final Clock clock;
    private BigDecimal balance;

    private final List<BankOperation> operations;

    public BankAccount() {
        this(UUID.randomUUID().toString(), Clock.systemUTC());
    }

    public BankAccount(String id, Clock clock) {
        this.id = id;
        this.clock = clock;
        this.balance = BigDecimal.ZERO;
        this.operations = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<BankOperation> getHistory() {
        return List.copyOf(operations);
    }

    public BankOperation deposit(Money amount) {
        this.balance = this.balance.add(amount.amount());
        BankOperation operation = BankOperation.deposit(amount, clock);
        this.operations.add(operation);
        return operation;
    }

    public BankOperation withdraw(Money amount) {
        BigDecimal newBalance = this.balance.subtract(amount.amount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant.");
        }
        this.balance = newBalance;
        BankOperation operation = BankOperation.withdrawal(amount, clock);
        this.operations.add(operation);
        return operation;
    }
}
