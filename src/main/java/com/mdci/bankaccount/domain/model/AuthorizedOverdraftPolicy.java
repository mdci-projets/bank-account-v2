package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;

public class AuthorizedOverdraftPolicy implements WithdrawalPolicy {

    @Override
    public void checkWithdrawal(BankAccount account, Money amount) {
        BigDecimal finalBalance = account.getBalance().subtract(amount.amount());
        BigDecimal maxOverdraft = account.getAuthorizedOverdraft().amount();

        if (finalBalance.compareTo(maxOverdraft.negate()) < 0) {
            throw new InsufficientBalanceException("Limite de découvert atteinte");
        }
    }
}

