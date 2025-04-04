package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import com.mdci.bankaccount.testutil.FakeBankOperationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BankOperationServiceTest {
    private BankAccountLoader accountLoader;
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
        accountLoader = mock(BankAccountLoader.class);
        operationFactory = new FakeBankOperationFactory(clock);
        service = new BankOperationService(accountLoader, accountRepository, operationRepository);
    }

    @Test
    void shouldDepositMoneyAndSaveOperation() {
        String accountId = "acc-123";
        BankAccount account = new BankAccount(accountId, operationFactory);
        when(accountLoader.loadWithHistory(accountId)).thenReturn(account);

        service.deposit(accountId, new Money(BigDecimal.valueOf(200)));

        assertEquals(BigDecimal.valueOf(200), account.getBalance());
        verify(operationRepository).save(any(BankAccount.class), any(BankOperation.class));
    }

    @Test
    void shouldWithdrawMoneyAndSaveOperation() {
        String accountId = "acc-123";
        BankAccount account = new BankAccount(accountId, operationFactory);
        BankOperation previousDeposit = operationFactory.deposit(new Money(BigDecimal.valueOf(150)));
        account.applyOperation(previousDeposit);

        when(accountLoader.loadWithHistory(accountId)).thenReturn(account);

        service.withdraw(accountId, new Money(BigDecimal.valueOf(50)));

        assertEquals(BigDecimal.valueOf(100), account.getBalance());
        verify(operationRepository).save(any(BankAccount.class), any(BankOperation.class));
    }

    @Test
    void shouldThrowWhenWithdrawingMoreThanBalance() {
        String accountId = "acc-123";
        BankAccount account = new BankAccount(accountId, operationFactory);
        when(accountLoader.loadWithHistory(accountId)).thenReturn(account);

        assertThrows(InsufficientBalanceException.class, () ->
                service.withdraw(accountId, new Money(BigDecimal.valueOf(100)))
        );
        verify(operationRepository, never()).save(any(), any());
    }

    @Test
    void shouldThrowWhenAccountNotFoundOnDeposit() {
        when(accountLoader.loadWithHistory("not-found"))
                .thenThrow(new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : not-found"));

        assertThrows(AccountNotFoundException.class, () ->
                service.deposit("not-found", new Money(BigDecimal.valueOf(100)))
        );
    }


    @Test
    void shouldComputeBalanceAtDate() {
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

        BigDecimal balance = service.getBalanceAtDate(accountId, date);

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
        String accountId = UUID.randomUUID().toString();
        BigDecimal overdraft = BigDecimal.valueOf(100);
        BigDecimal initialBalance = BigDecimal.ZERO;

        BankAccount account = new BankAccount(
                accountId,
                operationFactory,
                new Money(initialBalance),
                new Money(overdraft)
        );

        when(accountLoader.loadWithHistory(accountId)).thenReturn(account);

        service.withdraw(accountId, new Money(BigDecimal.valueOf(100)));

        assertEquals(BigDecimal.valueOf(-100), account.getBalance());
        verify(operationRepository, times(1)).save(any(), any());
    }

    @Test
    void shouldThrowWhenWithdrawalExceedsAuthorizedOverdraft() {
        String accountId = UUID.randomUUID().toString();
        BankAccount account = new BankAccount(
                accountId,
                operationFactory,
                new Money(BigDecimal.valueOf(0)),
                new Money(BigDecimal.valueOf(100))
        );

        when(accountLoader.loadWithHistory(accountId)).thenReturn(account);

        assertThrows(InsufficientBalanceException.class, () ->
                service.withdraw(accountId, new Money(BigDecimal.valueOf(101)))
        );

        verify(operationRepository, never()).save(any(), any());
    }
}
