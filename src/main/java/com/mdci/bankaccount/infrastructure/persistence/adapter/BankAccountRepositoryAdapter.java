package com.mdci.bankaccount.infrastructure.persistence.adapter;

import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankAccountJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankAccountEntityMapper;

import java.util.Optional;

public class BankAccountRepositoryAdapter implements IBankAccountRepository {
    private final BankAccountJpaRepository jpaRepository;
    private final BankAccountEntityMapper mapper;
    private final BankOperationFactory operationFactory;

    public BankAccountRepositoryAdapter(BankAccountJpaRepository jpaRepository,
                                        BankAccountEntityMapper mapper,
                                        BankOperationFactory factory) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.operationFactory = factory;
    }

    @Override
    public BankAccount save(BankAccount account) {
        BankAccountEntity entity = mapper.toEntity(account);

        BankAccountEntity saved = jpaRepository.save(entity);
        return new BankAccount(saved.getId(), operationFactory);
    }

    @Override
    public Optional<BankAccount> findById(String id) {
        return jpaRepository.findByIdWithOperations(id)
                .map(entity -> mapper.toDomainWithBalanceOnly(entity, operationFactory));
    }
}
