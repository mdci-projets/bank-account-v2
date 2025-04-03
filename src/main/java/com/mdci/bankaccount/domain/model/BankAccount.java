package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.time.Clock;
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
        if (initialBalance.isNegative()) {
            throw new InvalidAmountException("Le solde initial ne peut pas être négatif.");
        }
        this.id = id;
        this.balance = initialBalance != null ? initialBalance.amount() : BigDecimal.ZERO;
        this.operations = new ArrayList<>();
        this.operationFactory = operationFactory;
        this.authorizedOverdraft = authorizedOverdraft != null ? authorizedOverdraft : new Money(BigDecimal.ZERO);
        this.withdrawalPolicy = WithdrawalPolicyFactory.create(this);
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

    public void loadOperations(List<BankOperation> operations) {
        if (operations == null) {
            throw new IllegalArgumentException("La liste des opérations ne peut pas être nulle.");
        }

        this.operations.clear();
        this.operations.addAll(operations);

        // Recalcule le solde à partir de l'historique complet
        this.balance = computeBalanceFromOperations(operations);
    }

    public static BankAccount forTest(String accountId, BankOperationFactory factory, Money overdraft, List<BankOperation> operations) {
        BankAccount account = new BankAccount(accountId, factory, new Money(BigDecimal.ZERO), overdraft);

        operations.forEach(bankOperation -> account.applyOperation(bankOperation));
        return account;
    }

    public static BankAccount forTest(String accountId, Clock clock, List<BankOperation> operations) {
        return forTest(accountId, new BankOperationFactory(clock), new Money(BigDecimal.ZERO), operations);
    }
}
