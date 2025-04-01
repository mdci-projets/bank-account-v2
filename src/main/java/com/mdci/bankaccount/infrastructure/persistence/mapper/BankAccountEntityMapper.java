package com.mdci.bankaccount.infrastructure.persistence.mapper;

import com.mdci.bankaccount.domain.model.*;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankOperationEntity;

import java.util.List;
import java.util.stream.Collectors;

public class BankAccountEntityMapper {

    public BankAccountEntity toEntity(BankAccount account) {
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId(account.getId());
        entity.setCurrency("EUR");

        List<BankOperationEntity> operationEntities = account.getHistory().stream()
                .map(op -> toOperationEntity(op, entity))
                .collect(Collectors.toList());

        entity.setOperations(operationEntities);

        return entity;
    }

    public BankAccount toDomain(BankAccountEntity entity, BankOperationFactory factory) {
        List<BankOperation> operations = entity.getOperations().stream()
                .map(this::toOperation)
                .collect(Collectors.toList());

        return BankAccount.forTest(entity.getId(), factory, operations);
    }

    public BankAccount toDomainWithoutOperations(BankAccountEntity entity, BankOperationFactory factory) {
        return new BankAccount(entity.getId(), factory);
    }

    private BankOperationEntity toOperationEntity(BankOperation op, BankAccountEntity parent) {
        return new BankOperationEntity(
                op.id(),
                op.type().name(),
                op.amount().amount(),
                op.timestamp(),
                parent
        );
    }

    private BankOperation toOperation(BankOperationEntity entity) {
        return new BankOperation(
                entity.getId(),
                BankOperation.OperationType.valueOf(entity.getType()),
                new Money(entity.getAmount()),
                entity.getTimestamp()
        );
    }
}
