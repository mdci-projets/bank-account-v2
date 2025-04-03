package com.mdci.bankaccount.domain.model;

public interface WithdrawalPolicy {
    void checkWithdrawal(BankAccount account, Money amount);
}
