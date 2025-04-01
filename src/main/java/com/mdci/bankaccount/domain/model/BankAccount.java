package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {
    private final String id;
    private BigDecimal balance;
    private final List<BankOperation> operations;
    private final BankOperationFactory operationFactory;

    public BankAccount(String id, BankOperationFactory operationFactory) {
        this.id = id;
        this.balance = BigDecimal.ZERO;
        this.operations = new ArrayList<>();
        this.operationFactory = operationFactory;
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
        BankOperation operation = operationFactory.deposit(amount);
        this.operations.add(operation);
        return operation;
    }

    public BankOperation withdraw(Money amount) {
        BigDecimal newBalance = this.balance.subtract(amount.amount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant.");
        }
        this.balance = newBalance;
        BankOperation operation = operationFactory.withdrawal(amount);
        this.operations.add(operation);
        return operation;
    }
}
