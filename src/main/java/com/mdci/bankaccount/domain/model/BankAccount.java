package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
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

    public BigDecimal computeBalanceUntil(LocalDate date) {
        return operations.stream()
                .filter(op -> !op.timestamp().toLocalDate().isAfter(date))
                .map(BankOperation::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal computeBalanceFromOperations(List<BankOperation> operations) {
        return operations.stream()
                .map(BankOperation::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void loadOperations(List<BankOperation> operations) {
        if (operations == null) {
            throw new IllegalArgumentException("La liste des opérations ne peut pas être nulle.");
        }

        this.operations.clear();
        this.operations.addAll(operations);

        // Recalcule le solde à partir de l'historique complet
        this.balance = computeBalanceFromOperations(operations);
    }

    public static BankAccount forTest(String accountId, BankOperationFactory factory, List<BankOperation> operations) {
        BankAccount account = new BankAccount(accountId, factory);

        for (BankOperation op : operations) {
            switch (op.type()) {
                case DEPOSIT -> account.deposit(op.amount());
                case WITHDRAWAL -> account.withdraw(op.amount());
            }
        }

        return account;
    }

    public static BankAccount forTest(String accountId, Clock clock, List<BankOperation> operations) {
        return forTest(accountId, new BankOperationFactory(clock), operations);
    }
}
