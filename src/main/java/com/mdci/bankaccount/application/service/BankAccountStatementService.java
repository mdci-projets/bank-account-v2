package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.exception.FunctionalException;
import com.mdci.bankaccount.domain.model.AccountStatement;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.port.in.IBankAccountStatementService;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;

import java.time.LocalDateTime;
import java.util.List;

public class BankAccountStatementService implements IBankAccountStatementService {

    private final IBankAccountRepository accountRepository;
    private final IBankOperationRepository operationRepository;

    public BankAccountStatementService(IBankAccountRepository accountRepository, IBankOperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    public AccountStatement generateStatementForPeriod(String accountId, LocalDateTime from, LocalDateTime to) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Aucun compte trouvé avec l’ID : " + accountId));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime periodFrom = from != null ? from : now.minusMonths(1);
        LocalDateTime periodTo = to != null ? to : now;

        if (periodFrom.isAfter(periodTo)) {
            throw new FunctionalException("La date de début doit être antérieure à la date de fin.");
        }

        List<BankOperation> operations = operationRepository
                .findAllByAccountIdBetweenDates(accountId, periodFrom, periodTo);

        return new AccountStatement(
                account.getId(),
                account.getAccountType(),
                account.getBalance(),
                periodTo,
                periodFrom,
                periodTo,
                operations
        );
    }
}
