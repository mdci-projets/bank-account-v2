package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.domain.port.in.IBankOperationService;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BankOperationService implements IBankOperationService {
    private final IBankAccountRepository accountRepository;
    private final IBankOperationRepository operationRepository;

    public BankOperationService(IBankAccountRepository accountRepository,
                                IBankOperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    public void deposit(String accountId, Money amount) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : " + accountId));
        operationRepository.save(account, account.deposit(amount));
    }

    @Override
    public void withdraw(String accountId, Money amount) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : " + accountId));
        operationRepository.save(account, account.withdraw(amount));
    }

    @Override
    public BigDecimal getBalanceAtDate(String accountId, LocalDate date) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : " + accountId));

        List<BankOperation> operations = operationRepository.findAllByAccountIdUntilDate(accountId, date);
        return account.computeBalanceFromOperations(operations);
    }
}
