package com.mdci.bankaccount.infrastructure.persistence.mapper;

import com.mdci.bankaccount.domain.model.*;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountEntityMapperTest {

    private Clock clock;
    private BankOperationFactory operationFactory;
    private BankAccountEntityMapper mapper;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        operationFactory = new BankOperationFactory(clock);
        mapper = new BankAccountEntityMapper();
    }

    @Test
    void shouldMapDomainToEntityAndBack() {
        // Given
        String accountId = UUID.randomUUID().toString();
        BankAccount account = new BankAccount(accountId, operationFactory);
        account.deposit(new Money(BigDecimal.valueOf(100)));
        account.withdraw(new Money(BigDecimal.valueOf(30)));

        // When
        BankAccountEntity entity = mapper.toEntity(account);
        BankAccount mappedBack = mapper.toDomain(entity, operationFactory);

        // Then
        assertEquals(accountId, mappedBack.getId());
        assertEquals(account.getBalance(), mappedBack.getBalance());
        assertEquals(2, mappedBack.getHistory().size());

        BankOperation deposit = mappedBack.getHistory().get(0);
        BankOperation withdrawal = mappedBack.getHistory().get(1);

        assertEquals(BankOperation.OperationType.DEPOSIT, deposit.type());
        assertEquals(new Money(BigDecimal.valueOf(100)), deposit.amount());

        assertEquals(BankOperation.OperationType.WITHDRAWAL, withdrawal.type());
        assertEquals(new Money(BigDecimal.valueOf(30)), withdrawal.amount());
    }
}
