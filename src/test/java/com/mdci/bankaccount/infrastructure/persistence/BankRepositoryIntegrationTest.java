package com.mdci.bankaccount.infrastructure.persistence;

import com.mdci.bankaccount.domain.model.*;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import com.mdci.bankaccount.infrastructure.persistence.adapter.BankAccountRepositoryAdapter;
import com.mdci.bankaccount.infrastructure.persistence.adapter.BankOperationRepositoryAdapter;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankOperationEntity;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    private BankOperationEntityMapper mapper;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        factory = new FakeBankOperationFactory(clock);
        mapper = new BankOperationEntityMapper();

        accountRepository = new BankAccountRepositoryAdapter(
                accountJpaRepository,
                new BankAccountEntityMapper(),
                factory
        );

        operationRepository = new BankOperationRepositoryAdapter(
                operationJpaRepository,
                accountJpaRepository,
                mapper
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

    @Test
    void should_return_operations_between_dates() {
        // Given
        String accountId = "acc-100";
        BankAccountEntity account = new BankAccountEntity();
        account.setId(accountId);
        account.setAccountType(AccountType.COMPTE_COURANT);
        account.setAuthorizedOverdraft(BigDecimal.ZERO);
        account.setCurrency("EUR");
        accountJpaRepository.save(account);

        // Créer 3 opérations : 2 dans la période, 1 en dehors
        LocalDateTime now = LocalDateTime.now();

        operationJpaRepository.save(new BankOperationEntity(UUID.randomUUID().toString(), BankOperation.OperationType.DEPOSIT.name(), BigDecimal.valueOf(500), now.minusDays(10), account));
        operationJpaRepository.save(new BankOperationEntity(UUID.randomUUID().toString(), BankOperation.OperationType.WITHDRAWAL.name(), BigDecimal.valueOf(200), now.minusDays(5), account));
        operationJpaRepository.save(new BankOperationEntity(UUID.randomUUID().toString(), BankOperation.OperationType.DEPOSIT.name(), BigDecimal.valueOf(100), now.minusDays(40), account));

        BankOperationRepositoryAdapter adapter = new BankOperationRepositoryAdapter(operationJpaRepository, accountJpaRepository, mapper);

        // When
        List<BankOperation> results = adapter.findAllByAccountIdBetweenDates(
                accountId,
                now.minusDays(15),
                now
        );

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(op ->
                op.timestamp().isAfter(now.minusDays(15).minusSeconds(1)) &&
                        op.timestamp().isBefore(now.plusSeconds(1))
        );
    }
}
