package com.mdci.bankaccount.infrastructure.persistence.adapter;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankOperationEntity;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankAccountJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankOperationJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankOperationEntityMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BankOperationRepositoryAdapter implements IBankOperationRepository {

    private final BankOperationJpaRepository operationRepository;
    private final BankAccountJpaRepository accountRepository;
    private final BankOperationEntityMapper mapper;

    public BankOperationRepositoryAdapter(BankOperationJpaRepository operationRepository,
                                          BankAccountJpaRepository accountRepository,
                                          BankOperationEntityMapper mapper) {
        this.operationRepository = operationRepository;
        this.accountRepository = accountRepository;
        this.mapper = mapper;
    }

    @Override
    public BankOperation save(BankOperation operation) {
        BankAccountEntity account = accountRepository.findById(operation.id())
                .orElseThrow(() -> new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : " + operation.id()));

        BankOperationEntity savedEntity = operationRepository.save(mapper.toEntity(operation, account));
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<BankOperation> findAllByAccountIdUntilDate(String accountId, LocalDate date) {
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        return operationRepository.findAllByAccountIdUntilDate(accountId, endOfDay).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankOperation> findAllByAccountId(String accountId) {
        return operationRepository.findAllByAccountId(accountId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
