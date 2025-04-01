package com.mdci.bankaccount.domain.port.in;

import com.mdci.bankaccount.domain.model.BankAccount;

public interface IBankAccountService {
    BankAccount createAccount();

    BankAccount getAccount(String accountId);
}
