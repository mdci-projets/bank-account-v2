package com.mdci.bankaccount.infrastructure.persistence.adapter;

import com.mdci.bankaccount.domain.model.AccountType;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankAccountJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankAccountEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class BankAccountRepositoryAdapterTest {

    private BankAccountJpaRepository jpaRepository;
    private IBankAccountRepository adapter;
    private BankOperationFactory factory;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(BankAccountJpaRepository.class);
        BankAccountEntityMapper mapper = new BankAccountEntityMapper(); // non utilisé ici mais requis par signature
        factory = new BankOperationFactory(Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC));
        adapter = new BankAccountRepositoryAdapter(jpaRepository, mapper, factory);
    }

    @Test
    void shouldSaveAccountWithoutOperations() {
        // Given
        BankAccount account = new BankAccount("acc-123", factory);
        BankAccountEntity savedEntity = new BankAccountEntity();
        savedEntity.setId("acc-123");
        savedEntity.setAccountType(AccountType.COMPTE_COURANT);
        savedEntity.setCurrency("EUR");

        when(jpaRepository.save(any())).thenReturn(savedEntity);

        // When
        BankAccount result = adapter.save(account);

        // Then
        assertEquals("acc-123", result.getId());

        verify(jpaRepository).save(argThat(entity ->
                entity.getId().equals("acc-123") &&
                        entity.getCurrency().equals("EUR") &&
                        (entity.getOperations() == null || entity.getOperations().isEmpty())
        ));
    }

    @Test
    void shouldReturnAccountWithNoOperationsOnFind() {
        // Given
        String accountId = "acc-456";
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId(accountId);
        entity.setAccountType(AccountType.COMPTE_COURANT);
        entity.setCurrency("EUR");

        when(jpaRepository.findByIdWithOperations(accountId)).thenReturn(Optional.of(entity));

        // When
        Optional<BankAccount> result = adapter.findById(accountId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(accountId, result.get().getId());
        assertEquals(0, result.get().getBalance().compareTo(java.math.BigDecimal.ZERO));
        assertTrue(result.get().getHistory().isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenAccountNotFound() {
        when(jpaRepository.findById("not-found")).thenReturn(Optional.empty());

        Optional<BankAccount> result = adapter.findById("not-found");

        assertTrue(result.isEmpty());
    }

}
