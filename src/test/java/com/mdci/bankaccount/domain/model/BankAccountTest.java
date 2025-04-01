package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setUp() {
        account = new BankAccount();
    }

    @Test
    void shouldStartWithZeroBalance() {
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void shouldDepositMoney() {
        account.deposit(new Money(BigDecimal.valueOf(200)));
        assertEquals(BigDecimal.valueOf(200), account.getBalance());
    }

    @Test
    void shouldWithdrawMoney() {
        account.deposit(new Money(BigDecimal.valueOf(100)));
        account.withdraw(new Money(BigDecimal.valueOf(30)));
        assertEquals(BigDecimal.valueOf(70), account.getBalance());
    }

    @Test
    void shouldThrowWhenWithdrawingTooMuch() {
        account.deposit(new Money(BigDecimal.valueOf(50)));
        assertThrows(InsufficientBalanceException.class, () ->
                account.withdraw(new Money(BigDecimal.valueOf(100)))
        );
    }

    @Test
    void shouldGenerateUniqueAccountId() {
        assertNotNull(account.getId());
    }

    @Test
    void shouldRecordDepositOperation() {
        account.deposit(new Money(BigDecimal.valueOf(150)));

        List<BankOperation> history = account.getHistory();
        assertEquals(1, history.size());

        BankOperation op = history.get(0);
        assertEquals(BankOperation.OperationType.DEPOSIT, op.type());
        assertEquals(BigDecimal.valueOf(150), op.amount().amount());
        assertNotNull(op.timestamp());
    }

    @Test
    void shouldRecordWithdrawalOperation() {
        account.deposit(new Money(BigDecimal.valueOf(200)));
        account.withdraw(new Money(BigDecimal.valueOf(50)));

        List<BankOperation> history = account.getHistory();
        assertEquals(2, history.size());

        BankOperation op = history.get(1);
        assertEquals(BankOperation.OperationType.WITHDRAWAL, op.type());
        assertEquals(BigDecimal.valueOf(50), op.amount().amount());
    }

    @Test
    void shouldReturnUnmodifiableHistoryList() {
        // Given
        Clock fixedClock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        BankAccount account = new BankAccount(UUID.randomUUID().toString(), fixedClock);
        account.deposit(new Money(BigDecimal.valueOf(100)));

        // When / Then
        assertThrows(UnsupportedOperationException.class, () -> {
            account.getHistory().add(
                    BankOperation.deposit(new Money(BigDecimal.valueOf(50)), fixedClock)
            );
        });
    }

    @Test
    void shouldRegisterDepositAtFixedDate() {
        Clock fixedClock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        BankAccount account = new BankAccount(UUID.randomUUID().toString(), fixedClock);

        account.deposit(new Money(BigDecimal.valueOf(100)));

        BankOperation op = account.getHistory().get(0);
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), op.timestamp());
    }
}

