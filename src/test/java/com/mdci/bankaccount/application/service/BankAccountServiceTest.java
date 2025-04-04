package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountServiceTest {

    private IBankAccountRepository repository;
    private IBankOperationRepository operationRepository;
    private BankAccountService service;
    private BankOperationFactory operationFactory;
    private Clock clock;
    private BankAccountLoader accountLoader;

    @BeforeEach
    void setUp() {
        repository = mock(IBankAccountRepository.class);
        operationRepository = mock(IBankOperationRepository.class);
        accountLoader = mock(BankAccountLoader.class);
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        operationFactory = new BankOperationFactory(clock);
        service = new BankAccountService(operationRepository, repository, operationFactory, clock, accountLoader);
    }

    @Test
    void shouldCreateAccountAndSaveIt() {
        when(repository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankAccount account = service.createAccount(new Money(BigDecimal.ZERO), new Money(BigDecimal.ZERO));

        assertNotNull(account.getId());
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));

        ArgumentCaptor<BankAccount> captor = ArgumentCaptor.forClass(BankAccount.class);
        verify(repository).save(captor.capture());

        BankAccount savedAccount = captor.getValue();
        assertEquals(account.getId(), savedAccount.getId());
        assertEquals(account.getBalance(), savedAccount.getBalance());
    }

    @Test
    void shouldRetrieveAccountById() {
        String accountId = UUID.randomUUID().toString();
        BankAccount account = new BankAccount(accountId, operationFactory);
        when(accountLoader.loadWithHistory(accountId)).thenReturn(account);

        BankAccount result = service.getAccount(accountId);

        assertEquals(accountId, result.getId());
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        String accountId = "not-exist-id";
        when(accountLoader.loadWithHistory(accountId))
                .thenThrow(new AccountNotFoundException("Aucun compte trouvé pour l'identifiant : " + accountId));

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                service.getAccount(accountId));
        assertEquals("Aucun compte trouvé pour l'identifiant : " + accountId, exception.getMessage());
    }
}