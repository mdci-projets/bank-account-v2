package com.mdci.bankaccount.infrastructure.persistence.mapper;

import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankOperationEntity;

public class BankOperationEntityMapper {

    public BankOperationEntity toEntity(BankOperation op, BankAccountEntity parent) {
        return new BankOperationEntity(
                op.id(),
                op.type().name(),
                op.amount().amount(),
                op.timestamp(),
                parent
        );
    }

    public BankOperation toDomain(BankOperationEntity entity) {
        return new BankOperation(
                entity.getId(),
                BankOperation.OperationType.valueOf(entity.getType()),
                new Money(entity.getAmount()),
                entity.getTimestamp()
        );
    }
}
