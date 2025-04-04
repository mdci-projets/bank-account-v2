package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InvalidAmountException;
import com.mdci.bankaccount.domain.port.out.BankOperationFactory;
import com.mdci.bankaccount.testutil.FakeBankOperationFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BankOperationTest {

    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2025-01-01T12:00:00Z"),
            ZoneOffset.UTC
    );

    @Test
    void shouldCreateDepositOperationWithValidAmountAndClock() {
        // Given
        Money amount = new Money(BigDecimal.valueOf(100));
        BankOperationFactory operationFactory = new FakeBankOperationFactory(fixedClock);

        // When
        BankOperation operation = operationFactory.deposit(amount);

        // Then
        assertEquals(BankOperation.OperationType.DEPOSIT, operation.type());
        assertEquals(amount, operation.amount());
        assertEquals(LocalDateTime.ofInstant(fixedClock.instant(), fixedClock.getZone()), operation.timestamp());
        assertNotNull(operation.id());
    }

    @Test
    void shouldCreateWithdrawalOperationWithCustomTimestamp() {
        // Given
        Money amount = new Money(BigDecimal.valueOf(50));
        LocalDateTime timestamp = LocalDateTime.of(2024, 12, 25, 10, 0);
        String id = UUID.randomUUID().toString();

        // When
        BankOperation operation = new BankOperation(id, BankOperation.OperationType.WITHDRAWAL, amount, timestamp);

        // Then
        assertEquals(BankOperation.OperationType.WITHDRAWAL, operation.type());
        assertEquals(amount, operation.amount());
        assertEquals(timestamp, operation.timestamp());
        assertEquals(id, operation.id());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        assertThrows(InvalidAmountException.class, () ->
                new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.DEPOSIT, null, LocalDateTime.now())
        );
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThrows(InvalidAmountException.class, () ->
                new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.WITHDRAWAL,
                        new Money(BigDecimal.valueOf(-10)), LocalDateTime.now())
        );
    }

    @Test
    void shouldThrowExceptionWhenTimestampIsNull() {
        Money amount = new Money(BigDecimal.valueOf(200));
        assertThrows(InvalidAmountException.class, () ->
                new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.DEPOSIT, amount, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenIdIsBlank() {
        Money amount = new Money(BigDecimal.valueOf(200));
        assertThrows(InvalidAmountException.class, () ->
                new BankOperation(" ", BankOperation.OperationType.DEPOSIT, amount, LocalDateTime.now())
        );
    }

}

