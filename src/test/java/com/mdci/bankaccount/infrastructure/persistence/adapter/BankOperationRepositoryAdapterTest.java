package com.mdci.bankaccount.infrastructure.persistence.adapter;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankOperationEntity;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankAccountJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankOperationJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankOperationEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankOperationRepositoryAdapterTest {

    private BankOperationJpaRepository operationJpaRepository;
    private BankAccountJpaRepository accountJpaRepository;
    private BankOperationEntityMapper mapper;
    private BankOperationRepositoryAdapter adapter;

    private BankOperationFactory factory;

    private BankAccountEntity fakeAccount;

    @BeforeEach
    void setUp() {
        operationJpaRepository = mock(BankOperationJpaRepository.class);
        accountJpaRepository = mock(BankAccountJpaRepository.class);
        mapper = new BankOperationEntityMapper();
        adapter = new BankOperationRepositoryAdapter(operationJpaRepository, accountJpaRepository, mapper);

        fakeAccount = new BankAccountEntity();
        fakeAccount.setId("acc-123");

        factory = new BankOperationFactory(Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    void shouldSaveOperationForExistingAccount() {
        // Given
        String accountId = "account-id";
        BankAccount account = new BankAccount(accountId, factory);

        String operationId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.of(2025, 1, 1, 10, 0);

        BankOperation operation = new BankOperation(
                operationId,
                BankOperation.OperationType.DEPOSIT,
                new Money(BigDecimal.valueOf(200)),
                timestamp
        );

        when(accountJpaRepository.findById(accountId)).thenReturn(Optional.of(fakeAccount));

        BankOperationEntity savedEntity = new BankOperationEntity();
        savedEntity.setId(operationId);
        savedEntity.setType("DEPOSIT");
        savedEntity.setAmount(BigDecimal.valueOf(200));
        savedEntity.setTimestamp(timestamp);
        savedEntity.setAccount(fakeAccount);

        when(operationJpaRepository.save(any())).thenReturn(savedEntity);

        // When
        BankOperation result = adapter.save(account, operation);

        // Then
        assertNotNull(result);
        assertEquals(operationId, result.id());
        assertEquals(new Money(BigDecimal.valueOf(200)), result.amount());
        assertEquals(BankOperation.OperationType.DEPOSIT, result.type());

        verify(operationJpaRepository).save(any(BankOperationEntity.class));
    }

    @Test
    void shouldThrowIfAccountNotFoundWhenSavingOperation() {
        // Given
        String accountId = "non-existent-account-id";
        BankAccount account = new BankAccount(accountId, factory);

        BankOperation operation = new BankOperation(
                "operationId",
                BankOperation.OperationType.WITHDRAWAL,
                new Money(BigDecimal.valueOf(100)),
                LocalDateTime.of(2025, 1, 1, 11, 0)
        );

        when(accountJpaRepository.findById(accountId)).thenReturn(Optional.empty());

        // Then
        assertThrows(AccountNotFoundException.class, () -> adapter.save(account, operation));
        verify(operationJpaRepository, never()).save(any());
    }

    @Test
    void shouldReturnOperationsUntilGivenDate() {
        // Given
        String accountId = "acc-123";
        LocalDate date = LocalDate.of(2025, 1, 10);

        List<BankOperationEntity> entities = List.of(
                new BankOperationEntity(UUID.randomUUID().toString(), "DEPOSIT", BigDecimal.valueOf(100),
                        LocalDateTime.of(2025, 1, 5, 10, 0), fakeAccount),
                new BankOperationEntity(UUID.randomUUID().toString(), "WITHDRAWAL", BigDecimal.valueOf(30),
                        LocalDateTime.of(2025, 1, 8, 14, 0), fakeAccount)
        );

        when(operationJpaRepository.findAllByAccountIdUntilDate(eq(accountId), any())).thenReturn(entities);

        // When
        List<BankOperation> operations = adapter.findAllByAccountIdUntilDate(accountId, date);

        // Then
        assertEquals(2, operations.size());
        assertEquals(new Money(BigDecimal.valueOf(100)), operations.get(0).amount());
        assertEquals(BankOperation.OperationType.DEPOSIT, operations.get(0).type());
    }
}

