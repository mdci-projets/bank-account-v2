package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.domain.port.in.IBankOperationService;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.model.BankAccountFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BankOperationService implements IBankOperationService {
    private final IBankAccountRepository accountRepository;
    private final IBankOperationRepository operationRepository;
    private final BankAccountLoader accountLoader;

    public BankOperationService(BankAccountLoader accountLoader, IBankAccountRepository accountRepository,
                                IBankOperationRepository operationRepository) {
        this.accountLoader = accountLoader;
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    public void deposit(String accountId, Money amount) {
        BankAccount account = accountLoader.loadWithHistory(accountId);
        operationRepository.save(account, account.deposit(amount));
    }

    @Override
    public void withdraw(String accountId, Money amount) {
        BankAccount account = accountLoader.loadWithHistory(accountId);
        operationRepository.save(account, account.withdraw(amount));
    }

    @Override
    public BigDecimal getBalanceAtDate(String accountId, LocalDate date) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : " + accountId));

        List<BankOperation> operations = operationRepository.findAllByAccountIdUntilDate(accountId, date);
        return BankAccountFactory.computeBalanceFromOperations(operations);
    }
}
