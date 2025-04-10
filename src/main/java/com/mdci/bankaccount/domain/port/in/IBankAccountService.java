package com.mdci.bankaccount.domain.port.in;

import com.mdci.bankaccount.domain.model.AccountType;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.Money;

public interface IBankAccountService {
    BankAccount createAccount(Money initialBalance, Money authorizedOverdraft, AccountType accountType);
    public BankAccount createAccount(Money initialBalance, Money authorizedOverdraft);
    BankAccount getAccount(String accountId);
}
