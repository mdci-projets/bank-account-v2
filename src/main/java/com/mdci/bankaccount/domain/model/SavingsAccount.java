package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InvalidAmountException;

public class SavingsAccount extends BankAccount {
    private final Money depositCeiling;

    public SavingsAccount(
            String id,
            BankOperationFactory operationFactory,
            Money initialBalance,
            Money depositCeiling
    ) {

        super(id, initialBalance, Money.zero(), operationFactory, AccountType.LIVRET);
        this.depositCeiling = depositCeiling;
    }

    @Override
    public BankOperation deposit(Money amount) {
        Money newBalance = new Money(this.getBalance()).add(amount);
        if (newBalance.amount().compareTo(depositCeiling.amount()) > 0) {
            throw new InvalidAmountException("Le plafond du livret est dépassé.");
        }
        return super.deposit(amount);
    }

    public Money getDepositCeiling() {
        return depositCeiling;
    }
}