package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BankOperationServiceTest {
    private IBankAccountRepository accountRepository;
    private IBankOperationRepository operationRepository;
    private BankOperationService service;

    private Clock clock;
    private BankOperationFactory operationFactory;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        accountRepository = mock(IBankAccountRepository.class);
        operationRepository = mock(IBankOperationRepository.class);
        operationFactory = new BankOperationFactory(clock);
        service = new BankOperationService(accountRepository, operationRepository);
    }

    @Test
    void shouldDepositMoneyAndSaveOperation() {
        // Given
        String accountId = "acc-123";
        BankAccount account = new BankAccount(accountId, operationFactory);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // When
        service.deposit(accountId, new Money(BigDecimal.valueOf(200)));

        // Then
        assertEquals(BigDecimal.valueOf(200), account.getBalance());
        verify(operationRepository).save(any(BankAccount.class), any(BankOperation.class));
    }

    @Test
    void shouldWithdrawMoneyAndSaveOperation() {
        // Given
        String accountId = "acc-123";
        BankAccount account = new BankAccount(accountId, operationFactory);
        BankOperation previousDeposit = operationFactory.deposit(
                new Money(BigDecimal.valueOf(150)));
        List<BankOperation> operations = List.of(previousDeposit);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(operationRepository.findAllByAccountId(accountId)).thenReturn(operations);

        // When
        service.withdraw(accountId, new Money(BigDecimal.valueOf(50)));

        // Then
        assertEquals(BigDecimal.valueOf(100), account.getBalance());
        verify(operationRepository).save(any(BankAccount.class), any(BankOperation.class));
    }

    @Test
    void shouldThrowWhenWithdrawingMoreThanBalance() {
        // Given
        String accountId = "acc-123";
        BankAccount account = new BankAccount(accountId, operationFactory);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Then
        assertThrows(InsufficientBalanceException.class, () ->
                service.withdraw(accountId, new Money(BigDecimal.valueOf(100)))
        );
        verify(operationRepository, never()).save(any(), any());
    }

    @Test
    void shouldThrowWhenAccountNotFoundOnDeposit() {
        when(accountRepository.findById("not-found")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () ->
                service.deposit("not-found", new Money(BigDecimal.valueOf(100)))
        );
    }

    @Test
    void shouldComputeBalanceAtDate() {
        // Given
        String accountId = "acc-123";
        LocalDate date = LocalDate.of(2025, 1, 10);

        List<BankOperation> operations = List.of(
                new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.DEPOSIT,
                        new Money(BigDecimal.valueOf(100)), LocalDateTime.of(2025, 1, 5, 10, 0)),
                new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.WITHDRAWAL,
                        new Money(BigDecimal.valueOf(30)), LocalDateTime.of(2025, 1, 8, 14, 0))
        );

        BankAccount account = new BankAccount(accountId, operationFactory);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(operationRepository.findAllByAccountIdUntilDate(accountId, date)).thenReturn(operations);

        // When
        BigDecimal balance = service.getBalanceAtDate(accountId, date);

        // Then
        assertEquals(BigDecimal.valueOf(70), balance);
    }

    @Test
    void shouldThrowWhenAccountNotFoundOnGetBalanceAtDate() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () ->
                service.getBalanceAtDate("unknown", LocalDate.now())
        );
    }

    @Test
    void shouldAllowWithdrawalWhenWithinAuthorizedOverdraft() {
        // Given
        String accountId = UUID.randomUUID().toString();
        BigDecimal overdraft = BigDecimal.valueOf(100);
        BigDecimal initialBalance = BigDecimal.ZERO;

        BankAccount account = new BankAccount(
                accountId,
                operationFactory,
                new Money(initialBalance),
                new Money(overdraft)
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(operationRepository.findAllByAccountId(accountId)).thenReturn(new ArrayList<>());

        // When
        service.withdraw(accountId, new Money(BigDecimal.valueOf(100)));

        // Then
        assertEquals(BigDecimal.valueOf(-100), account.getBalance());
        verify(operationRepository, times(1)).save(any(), any());
    }

    @Test
    void shouldThrowWhenWithdrawalExceedsAuthorizedOverdraft() {
        // Given
        String accountId = UUID.randomUUID().toString();
        BankAccount account = new BankAccount(
                accountId,
                operationFactory,
                new Money(BigDecimal.valueOf(0)),
                new Money(BigDecimal.valueOf(100))
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(operationRepository.findAllByAccountId(accountId)).thenReturn(new ArrayList<>());

        // Then
        assertThrows(InsufficientBalanceException.class, () ->
                service.withdraw(accountId, new Money(BigDecimal.valueOf(101)))
        );

        verify(operationRepository, never()).save(any(), any());
    }


}
