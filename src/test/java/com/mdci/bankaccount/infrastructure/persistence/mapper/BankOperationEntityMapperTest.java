package com.mdci.bankaccount.infrastructure.persistence.mapper;

import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankOperationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BankOperationEntityMapperTest {

    private BankOperationEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BankOperationEntityMapper();
    }

    @Test
    void shouldMapDomainToEntityAndBackCorrectly() {
        // Given
        String id = UUID.randomUUID().toString();
        Money amount = new Money(BigDecimal.valueOf(150));
        BankOperation.OperationType type = BankOperation.OperationType.DEPOSIT;
        LocalDateTime timestamp = LocalDateTime.of(2025, 1, 10, 14, 30);

        BankOperation operation = new BankOperation(id, type, amount, timestamp);
        BankAccountEntity fakeAccount = new BankAccountEntity();
        fakeAccount.setId("acc-123");

        // When
        BankOperationEntity entity = mapper.toEntity(operation, fakeAccount);
        BankOperation mappedBack = mapper.toDomain(entity);

        // Then
        assertEquals(id, mappedBack.id());
        assertEquals(type, mappedBack.type());
        assertEquals(amount, mappedBack.amount());
        assertEquals(timestamp, mappedBack.timestamp());
        assertEquals(fakeAccount, entity.getAccount()); // vérifie bien que le lien a été établi
    }
}
