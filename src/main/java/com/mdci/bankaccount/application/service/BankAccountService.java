package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.port.in.IBankAccountService;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;

import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

public class BankAccountService implements IBankAccountService {

    private final IBankAccountRepository repository;
    private final BankOperationFactory operationFactory;
    private final Clock clock;

    public BankAccountService(IBankAccountRepository repository, BankOperationFactory operationFactory, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "Le repository ne doit pas être nul.");
        this.operationFactory = Objects.requireNonNull(operationFactory, "La factory d'opérations ne doit pas être nulle.");
        this.clock = Objects.requireNonNull(clock, "L'horloge (Clock) ne doit pas être nulle.");
    }

    @Override
    public BankAccount createAccount() {
        BankAccount account = new BankAccount(UUID.randomUUID().toString(), operationFactory);
        repository.save(account);
        return account;
    }

    @Override
    public BankAccount getAccount(String accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : " + accountId));
    }
}
