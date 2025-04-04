package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {
    private final String id;
    private BigDecimal balance;
    private final Money authorizedOverdraft;
    private final WithdrawalPolicy withdrawalPolicy;
    private final List<BankOperation> operations;
    private final BankOperationFactory operationFactory;

    public BankAccount(String id, BankOperationFactory operationFactory) {
        this(id, operationFactory, new Money(BigDecimal.ZERO), new Money(BigDecimal.ZERO));
    }

    public BankAccount(String id, BankOperationFactory operationFactory, Money initialBalance, Money authorizedOverdraft) {
        if (initialBalance == null) {
            throw new InvalidAmountException("Le solde initial est obligatoire.");
        }
        if (initialBalance.isNegative()) {
            throw new InvalidAmountException("Le solde initial ne peut pas être négatif.");
        }
        this.id = id;
        this.operations = new ArrayList<>();
        this.operationFactory = operationFactory;
        this.authorizedOverdraft = authorizedOverdraft != null ? authorizedOverdraft : new Money(BigDecimal.ZERO);
        this.withdrawalPolicy = WithdrawalPolicyFactory.create(this);
        this.balance = BigDecimal.ZERO;
        if (initialBalance != null && initialBalance.amount().compareTo(BigDecimal.ZERO) > 0) {
            this.deposit(initialBalance);
        }
    }

    BankAccount(String id, Money balance, Money authorizedOverdraft, BankOperationFactory factory) {
        this.id = id;
        this.balance = balance.amount();
        this.operationFactory = factory;
        this.authorizedOverdraft = authorizedOverdraft != null ? authorizedOverdraft : Money.zero();
        this.withdrawalPolicy = WithdrawalPolicyFactory.create(this);
        this.operations = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Money getAuthorizedOverdraft() {
        return authorizedOverdraft;
    }

    public List<BankOperation> getHistory() {
        return List.copyOf(operations);
    }

    public BankOperationFactory getOperationFactory() {
        return operationFactory;
    }

    public BankOperation deposit(Money amount) {
        this.balance = this.balance.add(amount.amount());
        BankOperation operation = operationFactory.deposit(amount);
        this.operations.add(operation);
        return operation;
    }

    public BankOperation withdraw(Money amount) {
        withdrawalPolicy.checkWithdrawal(this, amount);
        this.balance = this.balance.subtract(amount.amount());
        ;
        BankOperation operation = operationFactory.withdrawal(amount);
        this.operations.add(operation);
        return operation;
    }

    public void applyOperation(BankOperation operation) {
        this.balance = this.balance.add(operation.value());
        this.operations.add(operation);
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

    void loadOperations(List<BankOperation> operations) {
        if (operations == null) {
            throw new IllegalArgumentException("La liste des opérations ne peut pas être nulle.");
        }

        this.operations.clear();
        this.operations.addAll(operations);

        // Recalcule le solde à partir de l'historique complet
        this.balance = computeBalanceFromOperations(operations);
    }
}
