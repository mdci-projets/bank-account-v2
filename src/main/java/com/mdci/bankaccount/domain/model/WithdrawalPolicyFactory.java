package com.mdci.bankaccount.domain.model;

import java.math.BigDecimal;

public class WithdrawalPolicyFactory {

    public static WithdrawalPolicy create(BankAccount account) {
        if (account.getAuthorizedOverdraft().amount().compareTo(BigDecimal.ZERO) > 0) {
            return new AuthorizedOverdraftPolicy();
        }
        return new NoOverdraftPolicy();
    }
}
