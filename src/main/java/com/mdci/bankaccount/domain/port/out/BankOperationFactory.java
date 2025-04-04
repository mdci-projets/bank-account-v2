package com.mdci.bankaccount.domain.port.out;

import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.Money;

public interface BankOperationFactory {
    BankOperation deposit(Money amount);

    BankOperation withdrawal(Money amount);
}