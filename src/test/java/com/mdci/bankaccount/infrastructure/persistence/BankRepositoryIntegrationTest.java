package com.mdci.bankaccount.infrastructure.persistence;

import com.mdci.bankaccount.domain.model.*;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import com.mdci.bankaccount.infrastructure.persistence.adapter.BankAccountRepositoryAdapter;
import com.mdci.bankaccount.infrastructure.persistence.adapter.BankOperationRepositoryAdapter;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankAccountJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankOperationJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankAccountEntityMapper;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankOperationEntityMapper;
import com.mdci.bankaccount.testutil.FakeBankOperationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class BankRepositoryIntegrationTest {

    @Autowired
    private BankAccountJpaRepository accountJpaRepository;

    @Autowired
    private BankOperationJpaRepository operationJpaRepository;

    private IBankAccountRepository accountRepository;
    private IBankOperationRepository operationRepository;
    private BankOperationFactory factory;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        factory = new FakeBankOperationFactory(clock);

        accountRepository = new BankAccountRepositoryAdapter(
                accountJpaRepository,
                new BankAccountEntityMapper(),
                factory
        );

        operationRepository = new BankOperationRepositoryAdapter(
                operationJpaRepository,
                accountJpaRepository,
                new BankOperationEntityMapper()
        );
    }

    @Test
    void shouldPersistAndRetrieveAccountAndOperation() {
        // Given
        String accountId = UUID.randomUUID().toString();
        BankAccount account = new BankAccount(accountId, factory);
        accountRepository.save(account);

        var accountEntity = accountJpaRepository.findById(accountId).orElseThrow();

        BankOperation deposit = account.deposit(new Money(BigDecimal.valueOf(100)));
        var operationEntity = new BankOperationEntityMapper().toEntity(deposit, accountEntity);
        operationJpaRepository.save(operationEntity);

        // When
        Optional<BankAccount> loadedAccount = accountRepository.findById(accountId);
        List<BankOperation> ops = operationRepository.findAllByAccountIdUntilDate(accountId, LocalDate.of(2025, 1, 2));

        // Then
        assertTrue(loadedAccount.isPresent());
        assertEquals(accountId, loadedAccount.get().getId());

        assertEquals(1, ops.size());
        assertEquals(BankOperation.OperationType.DEPOSIT, ops.get(0).type());
        assertEquals(new Money(BigDecimal.valueOf(100)), ops.get(0).amount());
    }

    @Test
    void shouldSaveOperationUsingRepositoryAdapter() {
        // Given
        String accountId = UUID.randomUUID().toString();
        BankAccount account = new BankAccount(accountId, factory);
        accountRepository.save(account);

        BankOperation deposit = account.deposit(new Money(BigDecimal.valueOf(200)));

        // When
        BankOperation saved = ((BankOperationRepositoryAdapter) operationRepository).save(account, deposit);

        // Then
        assertNotNull(saved);
        assertEquals(BankOperation.OperationType.DEPOSIT, saved.type());
        assertEquals(new Money(BigDecimal.valueOf(200)), saved.amount());

        List<BankOperation> found = operationRepository.findAllByAccountIdUntilDate(accountId, LocalDate.of(2025, 1, 2));
        assertEquals(1, found.size());
        assertEquals(new Money(BigDecimal.valueOf(200)), found.get(0).amount());
    }
}
