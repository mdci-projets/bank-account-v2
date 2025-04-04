package com.mdci.bankaccount.domain.model;

public interface BankOperationFactory {
    BankOperation deposit(Money amount);

    BankOperation withdrawal(Money amount);
}