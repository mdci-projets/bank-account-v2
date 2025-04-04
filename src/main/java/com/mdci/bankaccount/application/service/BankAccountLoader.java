package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankAccountFactory;
import com.mdci.bankaccount.domain.model.BankOperation;
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
                history
        );
    }

    public BankAccount loadWithoutHistory(String accountId) {
        BankAccount base = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : " + accountId));

        List<BankOperation> history = operationRepository.findAllByAccountId(accountId);




        return BankAccountFactory.rehydrate(
                base.getId(),
                base.getOperationFactory(),
                base.getAuthorizedOverdraft(),
                history
        );
    }
}
