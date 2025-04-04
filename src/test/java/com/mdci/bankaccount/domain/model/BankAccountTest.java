package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;
import com.mdci.bankaccount.domain.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    private BankAccount account;
    private Clock fixedClock;
    private BankOperationFactory operationFactory;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        operationFactory = new BankOperationFactory(fixedClock);
        account = new BankAccount(UUID.randomUUID().toString(), operationFactory);
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
        operationFactory = new BankOperationFactory(fixedClock);
        BankAccount account = new BankAccount(UUID.randomUUID().toString(), operationFactory);

        account.deposit(new Money(BigDecimal.valueOf(100)));

        // When / Then
        assertThrows(UnsupportedOperationException.class, () -> {
            account.getHistory().add(
                    operationFactory.deposit(new Money(BigDecimal.valueOf(50)))
            );
        });
    }

    @Test
    void shouldRegisterDepositAtFixedDate() {
        Clock fixedClock = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);
        operationFactory = new BankOperationFactory(fixedClock);
        BankAccount account = new BankAccount(UUID.randomUUID().toString(), operationFactory);

        account.deposit(new Money(BigDecimal.valueOf(100)));

        BankOperation op = account.getHistory().get(0);
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), op.timestamp());
    }

    @Test
    void should_allow_withdrawal_with_authorized_overdraft() {
        // Given
        BankAccount account = BankAccountFactory.create(UUID.randomUUID().toString(), new Money(BigDecimal.valueOf(100)), new Money(BigDecimal.valueOf(50)), operationFactory, AccountType.COMPTE_COURANT, new Money(BigDecimal.ZERO));
        // When
        account.withdraw(new Money(BigDecimal.valueOf(150)));

        // Then
        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(-50));
    }

    @Test
    void should_throw_if_withdrawal_exceeds_overdraft_limit() {
        BankAccount account = new BankAccount(UUID.randomUUID().toString(), operationFactory, new Money(BigDecimal.valueOf(100)), new Money(BigDecimal.valueOf(50)));

        assertThatThrownBy(() -> account.withdraw(new Money(BigDecimal.valueOf(151))))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Limite de découvert atteinte");
    }

    @Test
    void shouldInitializeBalanceAndOverdraftToZeroByDefault() {
        // When
        BankAccount account = new BankAccount(UUID.randomUUID().toString(), operationFactory);

        // Then
        assertEquals(BigDecimal.ZERO, account.getBalance(), "Le solde initial doit être zéro.");
        assertEquals(BigDecimal.ZERO, account.getAuthorizedOverdraft().amount(), "Le découvert autorisé doit être zéro.");
    }

    @Test
    void should_throw_if_initial_balance_is_negative() {
        assertThatThrownBy(() -> new BankAccount(UUID.randomUUID().toString(), operationFactory, new Money(BigDecimal.valueOf(-100)), new Money(BigDecimal.ZERO)))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void should_create_account_with_authorized_overdraft() {
        // When
        BankAccount account = BankAccountFactory.create(UUID.randomUUID().toString(), new Money(BigDecimal.valueOf(50)), new Money(BigDecimal.valueOf(100)), operationFactory, AccountType.COMPTE_COURANT, new Money(BigDecimal.ZERO));

        // Then
        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(50));
        assertThat(account.getAuthorizedOverdraft().amount()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void shouldApplyDepositOperationCorrectly() {
        // Given
        Clock fixedClock = Clock.fixed(Instant.parse("2025-04-10T14:30:00Z"), ZoneOffset.UTC);
        BankOperationFactory factory = new BankOperationFactory(fixedClock);
        BankAccount account = new BankAccount("acc-123", factory);

        BankOperation operation = new BankOperation(
                "op-001",
                BankOperation.OperationType.DEPOSIT,
                new Money(BigDecimal.valueOf(100)),
                LocalDateTime.of(2025, 4, 10, 14, 30)
        );

        // When
        account.applyOperation(operation);

        // Then
        assertEquals(BigDecimal.valueOf(100), account.getBalance());
        assertEquals(1, account.getHistory().size());
        assertEquals("op-001", account.getHistory().get(0).id());
        assertEquals(LocalDateTime.of(2025, 4, 10, 14, 30), account.getHistory().get(0).timestamp());
    }

    @Test
    void shouldApplyWithdrawalOperationCorrectly() {
        // Given
        Clock fixedClock = Clock.fixed(Instant.parse("2025-04-10T14:30:00Z"), ZoneOffset.UTC);
        BankOperationFactory factory = new BankOperationFactory(fixedClock);
        BankAccount account = BankAccountFactory.create("acc-456", new Money(BigDecimal.valueOf(200)), new Money(BigDecimal.ZERO), factory, AccountType.COMPTE_COURANT, new Money(BigDecimal.ZERO));

        BankOperation withdrawal = new BankOperation(
                "op-002",
                BankOperation.OperationType.WITHDRAWAL,
                new Money(BigDecimal.valueOf(50)),
                LocalDateTime.of(2025, 4, 10, 14, 30)
        );

        // When
        account.applyOperation(withdrawal);

        // Then
        assertEquals(BigDecimal.valueOf(150), account.getBalance());
        // Il ya aussi l'opération du solde initiale
        assertEquals(2, account.getHistory().size());
        assertEquals("op-002", account.getHistory().get(1).id());
    }

}

