package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.model.*;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;

import java.util.List;

public class BankAccountLoader {

    private final IBankAccountRepository accountRepository;
    private final IBankOperationRepository operationRepository;

    public BankAccountLoader(IBankAccountRepository accountRepository,
                             IBankOperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    public BankAccount loadWithHistory(String accountId) {
        BankAccount base = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : " + accountId));

        List<BankOperation> history = operationRepository.findAllByAccountId(accountId);

        return BankAccountFactory.rehydrate(
                base.getId(),
                base.getOperationFactory(),
                base.getAuthorizedOverdraft(),
                history,
                base.getAccountType(),
                getDepositCeiling(base)
        );
    }

    public BankAccount loadWithoutHistory(String accountId) {
        BankAccount base = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : " + accountId));

        // on va juste reconstituer le solde sans les opérations
        Money balance = new Money(base.getBalance());

        return BankAccountFactory.rehydrateWithBalanceOnly(
                base.getId(),
                base.getOperationFactory(),
                balance,
                base.getAuthorizedOverdraft(),
                base.getAccountType(),
                getDepositCeiling(base)
        );
    }

    private Money getDepositCeiling(BankAccount base) {
        if (base instanceof SavingsAccount savingsAccount) {
            return savingsAccount.getDepositCeiling();
        }
        return Money.zero();
    }
}
