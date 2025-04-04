package com.mdci.bankaccount.infrastructure.persistence.mapper;

import com.mdci.bankaccount.domain.model.*;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankOperationEntity;
import com.mdci.bankaccount.testutil.FakeBankOperationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BankAccountEntityMapperTest {

    private Clock clock;
    private BankOperationFactory operationFactory;
    private BankAccountEntityMapper mapper;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        operationFactory = new FakeBankOperationFactory(clock);
        mapper = new BankAccountEntityMapper();
    }

    @Test
    void shouldMapDomainToEntityAndBack() {
        // Given
        String accountId = UUID.randomUUID().toString();
        BankAccount account = new BankAccount(accountId, operationFactory);
        account.deposit(new Money(BigDecimal.valueOf(100)));
        account.withdraw(new Money(BigDecimal.valueOf(30)));

        // When
        BankAccountEntity entity = mapper.toEntity(account);

        // Simuler la génération d'ID des opérations par JPA
        int i = 0;
        for (BankOperationEntity opEntity : entity.getOperations()) {
            opEntity.setId(UUID.randomUUID().toString());
            opEntity.setTimestamp(LocalDateTime.of(2025, 1, 1, 0, 0).plusMinutes(i++));
        }
        BankAccount mappedBack = mapper.toDomain(entity, operationFactory);

        // Then
        assertEquals(accountId, mappedBack.getId());
        assertEquals(account.getBalance(), mappedBack.getBalance());
        assertEquals(2, mappedBack.getHistory().size());

        BankOperation deposit = mappedBack.getHistory().get(0);
        BankOperation withdrawal = mappedBack.getHistory().get(1);

        assertEquals(BankOperation.OperationType.DEPOSIT, deposit.type());
        assertEquals(new Money(BigDecimal.valueOf(100)), deposit.amount());

        assertEquals(BankOperation.OperationType.WITHDRAWAL, withdrawal.type());
        assertEquals(new Money(BigDecimal.valueOf(30)), withdrawal.amount());
    }

    @Test
    void should_map_to_entity_correctly() {
        // Given
        Money balance = Money.of(new BigDecimal("100.00"));
        Money overdraft = Money.of(new BigDecimal("50.00"));
        BankAccount account = BankAccountFactory.create("ACC123", balance, overdraft, operationFactory, AccountType.COMPTE_COURANT, new Money(BigDecimal.ZERO));

        account.deposit(Money.of(BigDecimal.TEN));
        account.withdraw(Money.of(BigDecimal.ONE));

        // When
        BankAccountEntity entity = mapper.toEntity(account);

        // Then
        assertThat(entity.getId()).isEqualTo("ACC123");
        assertThat(entity.getAuthorizedOverdraft()).isEqualByComparingTo("50.00");
        // Il ya aussi l'opération du solde initiale
        assertThat(entity.getOperations()).hasSize(3);

    }

    @Test
    void should_map_to_domain_correctly() {
        // Given
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId("ACC001");
        entity.setAuthorizedOverdraft(new BigDecimal("100.00"));
        entity.setAccountType(AccountType.COMPTE_COURANT);
        entity.setDepositCeiling(BigDecimal.ZERO);

        LocalDateTime timestamp = LocalDateTime.of(2025, 4, 10, 14, 30);
        entity.setOperations(List.of(
                new BankOperationEntity("op1", "DEPOSIT", new BigDecimal("100.00"), timestamp, entity),
                new BankOperationEntity("op2", "WITHDRAWAL", new BigDecimal("20.00"), timestamp, entity)
        ));

        // When
        BankAccount domain = mapper.toDomain(entity, operationFactory);

        // Then
        assertThat(domain.getId()).isEqualTo("ACC001");
        assertThat(domain.getAuthorizedOverdraft().amount()).isEqualByComparingTo("100.00");
        assertThat(domain.getAccountType()).isEqualTo(AccountType.COMPTE_COURANT);
        assertThat(domain.getHistory()).hasSize(2);
    }

    @Test
    void should_map_to_domain_without_operations_and_compute_balance() {
        // Given
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId("ACC002");
        entity.setAuthorizedOverdraft(new BigDecimal("25.00"));
        entity.setAccountType(AccountType.COMPTE_COURANT);
        LocalDateTime timestamp = LocalDateTime.of(2025, 4, 10, 14, 30);
        entity.setOperations(List.of(
                new BankOperationEntity("op1", "DEPOSIT", new BigDecimal("100.00"), timestamp, entity),
                new BankOperationEntity("op2", "WITHDRAWAL", new BigDecimal("30.00"), timestamp, entity)
        ));

        List<BankOperation> operations =  List.of(
                new BankOperation("op1", BankOperation.OperationType.DEPOSIT, Money.of(new BigDecimal("100.00")), timestamp),
                new BankOperation("op2", BankOperation.OperationType.WITHDRAWAL, Money.of(new BigDecimal("30.00")), timestamp)
        );

        // When
        BankAccount domain = mapper.toDomainWithBalanceOnly(entity, operationFactory, operations);

        // Then
        assertThat(domain.getId()).isEqualTo("ACC002");
        assertThat(domain.getBalance()).isEqualByComparingTo("70.00");
        assertThat(domain.getAuthorizedOverdraft().amount()).isEqualByComparingTo("25.00");
        assertThat(domain.getHistory()).isEmpty();
    }

    @Test
    void should_map_livret_account_to_entity_and_back() {
        // Arrange
        SavingsAccount account = new SavingsAccount("ACC001", operationFactory, Money.of(BigDecimal.valueOf(1000)), Money.of(BigDecimal.valueOf(22950)));

        BankAccountEntity entity = mapper.toEntity(account);

        // Act
        BankAccount reconstructed = mapper.toDomain(entity, operationFactory);

        // Assert
        assertThat(reconstructed.getId()).isEqualTo("ACC001");
        assertThat(reconstructed.getAccountType()).isEqualTo(AccountType.LIVRET);
        assertThat(((SavingsAccount) reconstructed).getDepositCeiling().amount())
                .isEqualByComparingTo("22950");
    }

    @Test
    void should_map_compte_courant_account() {
        BankAccount account = new BankAccount("ACC002", operationFactory, Money.of(BigDecimal.valueOf(500)), Money.of(BigDecimal.valueOf(2000)));

        // mapping aller
        BankAccountEntity entity = mapper.toEntity(account);

        BankOperationEntity op = new BankOperationEntity(
                UUID.randomUUID().toString(),
                "DEPOSIT",
                BigDecimal.valueOf(500),
                LocalDateTime.now(),
                entity
        );
        entity.setOperations(List.of(op));

        // mapping retour
        BankAccount result = mapper.toDomain(entity, operationFactory);

        assertThat(result.getAuthorizedOverdraft().amount()).isEqualByComparingTo("2000");
        assertThat(result.getAccountType()).isEqualTo(AccountType.COMPTE_COURANT);
    }
}
