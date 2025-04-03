package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;

public class NoOverdraftPolicy implements WithdrawalPolicy {

    @Override
    public void checkWithdrawal(BankAccount account, Money amount) {
        if (account.getBalance().compareTo(amount.amount()) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant.");
        }
    }
}
