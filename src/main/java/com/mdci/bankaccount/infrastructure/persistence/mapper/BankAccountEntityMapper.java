package com.mdci.bankaccount.infrastructure.persistence.mapper;

import com.mdci.bankaccount.domain.model.*;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankOperationEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class BankAccountEntityMapper {

    public BankAccountEntity toEntity(BankAccount account) {
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId(account.getId());
        entity.setAuthorizedOverdraft(account.getAuthorizedOverdraft().amount());
        entity.setCurrency("EUR");
        entity.setAccountType(account.getAccountType());

        if (account instanceof SavingsAccount savings) {
            entity.setDepositCeiling(savings.getDepositCeiling().amount());
        }

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

        return BankAccountFactory.rehydrate(
                entity.getId(),
                factory,
                Money.of(entity.getAuthorizedOverdraft()),
                operations,
                entity.getAccountType(),
                Money.of(entity.getDepositCeiling() != null ? entity.getDepositCeiling() : BigDecimal.ZERO)
        );
    }

    public BankAccount toDomainWithBalanceOnly(BankAccountEntity entity, BankOperationFactory factory, List<BankOperation> operations) {
        BigDecimal balance = computeBalanceFromOperations(operations);

        return BankAccountFactory.rehydrateWithBalanceOnly(
                entity.getId(),
                factory,
                Money.of(balance),
                Money.of(entity.getAuthorizedOverdraft()),
                entity.getAccountType(),
                Money.of(entity.getDepositCeiling() != null ? entity.getDepositCeiling() : BigDecimal.ZERO)
        );
    }

    private BigDecimal computeBalanceFromOperations(List<BankOperation> operations) {
        BigDecimal balance = BigDecimal.ZERO;
        for (BankOperation op : operations) {
            switch (BankOperation.OperationType.valueOf(op.type().name())) {
                case DEPOSIT -> balance = balance.add(op.amount().amount());
                case WITHDRAWAL -> balance = balance.subtract(op.amount().amount());
            }
        }
        return balance;
    }

    private BankOperationEntity toOperationEntity(BankOperation op, BankAccountEntity parent) {
        return new BankOperationEntity(
                null,
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
