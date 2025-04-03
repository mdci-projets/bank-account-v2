package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountServiceTest {

    private IBankAccountRepository repository;
    private IBankOperationRepository operationRepository;
    private BankAccountService service;
    private BankOperationFactory operationFactory;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = mock(IBankAccountRepository.class);
        operationRepository = mock(IBankOperationRepository.class);
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        operationFactory = new BankOperationFactory(clock);
        service = new BankAccountService(operationRepository, repository, operationFactory, clock);
    }

    @Test
    void shouldCreateAccountAndSaveIt() {
        // When
        BankAccount account = service.createAccount(new Money(BigDecimal.ZERO), new Money(BigDecimal.ZERO));

        // Then
        assertNotNull(account.getId());
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));
        verify(repository).save(account);
    }

    @Test
    void shouldRetrieveAccountById() {
        // Given
        String accountId = UUID.randomUUID().toString();
        BankAccount account = new BankAccount(accountId, operationFactory);
        when(repository.findById(accountId)).thenReturn(Optional.of(account));
        when(operationRepository.findAllByAccountId(accountId)).thenReturn(new ArrayList<>());

        // When
        BankAccount result = service.getAccount(accountId);

        // Then
        assertEquals(accountId, result.getId());
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        // Given
        String accountId = "not-exist-id";
        when(repository.findById(accountId)).thenReturn(Optional.empty());

        // Then
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                service.getAccount(accountId));
        assertEquals("Aucun compte trouvé pour l'identifiant : " + accountId, exception.getMessage());
    }
}
