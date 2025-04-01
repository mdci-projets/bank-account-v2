package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;

public class BankAccount {

    private final String id;

    private final Clock clock;
    private BigDecimal balance;

    public BankAccount() {
        this(UUID.randomUUID().toString(), Clock.systemUTC());
    }

    public BankAccount(String id, Clock clock) {
        this.id = id;
        this.clock = clock;
        this.balance = BigDecimal.ZERO;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BankOperation deposit(Money amount) {
        this.balance = this.balance.add(amount.amount());
        return BankOperation.deposit(amount, clock);
    }

    public BankOperation withdraw(Money amount) {
        BigDecimal newBalance = this.balance.subtract(amount.amount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant.");
        }
        this.balance = newBalance;
        return BankOperation.withdrawal(amount, clock);
    }
}
