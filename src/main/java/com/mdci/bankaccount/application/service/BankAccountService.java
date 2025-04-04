package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.model.*;
import com.mdci.bankaccount.domain.port.in.IBankAccountService;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;

import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

public class BankAccountService implements IBankAccountService {

    private final IBankAccountRepository repository;
    private final IBankOperationRepository operationRepository;
    private final BankOperationFactory operationFactory;
    private final Clock clock;
    private final BankAccountLoader accountLoader;

    public BankAccountService(IBankOperationRepository operationRepository, IBankAccountRepository repository, BankOperationFactory operationFactory, Clock clock,
                              BankAccountLoader accountLoader) {
        this.operationRepository = Objects.requireNonNull(operationRepository, "Le operation repository ne doit pas être nul.");
        this.repository = Objects.requireNonNull(repository, "Le account repository ne doit pas être nul.");
        this.operationFactory = Objects.requireNonNull(operationFactory, "La factory d'opérations ne doit pas être nulle.");
        this.clock = Objects.requireNonNull(clock, "L'horloge (Clock) ne doit pas être nulle.");
        this.accountLoader = accountLoader;
    }

    @Override
    public BankAccount createAccount(Money initialBalance, Money authorizedOverdraft) {
        BankAccount account = new BankAccount(UUID.randomUUID().toString(), operationFactory, initialBalance, authorizedOverdraft);
        BankOperation initOp = null;
        if (!account.getHistory().isEmpty()) {
            initOp = account.getHistory().get(0);
        }
        account = repository.save(account);
        if (initOp != null) {
            operationRepository.save(account, initOp);
            account = BankAccountFactory.rehydrateWithBalanceOnly(account.getId(), account.getOperationFactory(), initialBalance, account.getAuthorizedOverdraft());
        }
        return account;
    }

    @Override
    public BankAccount getAccount(String accountId) {
        return accountLoader.loadWithHistory(accountId);
    }
}
