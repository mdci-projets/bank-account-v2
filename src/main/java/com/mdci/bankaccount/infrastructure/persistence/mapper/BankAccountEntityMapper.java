package com.mdci.bankaccount.infrastructure.persistence.mapper;

import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.model.Money;
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
        Money overdraft = Money.of(entity.getAuthorizedOverdraft());
        return BankAccount.forTest(entity.getId(), factory, overdraft, operations);
    }

    public BankAccount toDomainWithBalanceOnly(BankAccountEntity entity, BankOperationFactory factory) {
        Money overdraft = Money.of(entity.getAuthorizedOverdraft());
        BigDecimal balance = computeBalanceFromOperations(entity.getOperations());
        return new BankAccount(entity.getId(), factory, new Money(balance), overdraft);
    }

    private BigDecimal computeBalanceFromOperations(List<BankOperationEntity> operations) {
        BigDecimal balance = BigDecimal.ZERO;
        for (BankOperationEntity op : operations) {
            switch (BankOperation.OperationType.valueOf(op.getType())) {
                case DEPOSIT -> balance = balance.add(op.getAmount());
                case WITHDRAWAL -> balance = balance.add(op.getAmount().negate());
            }
        }

        return balance;
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
