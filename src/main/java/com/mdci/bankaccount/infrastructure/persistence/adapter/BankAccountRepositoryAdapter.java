package com.mdci.bankaccount.infrastructure.persistence.adapter;

import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankAccountJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankAccountEntityMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
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
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId(account.getId());
        entity.setCurrency("EUR");

        BankAccountEntity saved = jpaRepository.save(entity);
        return new BankAccount(saved.getId(), operationFactory);
    }

    @Override
    public Optional<BankAccount> findById(String id) {
        return jpaRepository.findById(id)
                .map(entity -> mapper.toDomainWithoutOperations(entity, operationFactory));
    }
}
