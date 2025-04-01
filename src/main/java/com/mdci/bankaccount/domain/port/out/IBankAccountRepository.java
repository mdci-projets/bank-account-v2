package com.mdci.bankaccount.domain.port.out;

import com.mdci.bankaccount.domain.model.BankAccount;

import java.util.Optional;

public interface IBankAccountRepository {
    Optional<BankAccount> findById(String accountId);

    BankAccount save(BankAccount account);
}
