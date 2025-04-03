package com.mdci.bankaccount.infrastructure.persistence.mapper;

import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import com.mdci.bankaccount.infrastructure.persistence.entity.BankOperationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountEntityMapperTest {

    private Clock clock;
    private BankOperationFactory operationFactory;
    private BankAccountEntityMapper mapper;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);
        operationFactory = new BankOperationFactory(clock);
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
        BankAccount account = new BankAccount("ACC123", operationFactory, balance, overdraft);
        account.deposit(Money.of(BigDecimal.TEN));
        account.withdraw(Money.of(BigDecimal.ONE));

        // When
        BankAccountEntity entity = mapper.toEntity(account);

        // Then
        assertThat(entity.getId()).isEqualTo("ACC123");
        assertThat(entity.getAuthorizedOverdraft()).isEqualByComparingTo("50.00");
        assertThat(entity.getOperations()).hasSize(2);

    }

    @Test
    void should_map_to_domain_correctly() {
        // Given
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId("ACC001");
        entity.setAuthorizedOverdraft(new BigDecimal("100.00"));
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
        assertThat(domain.getHistory()).hasSize(2);

        // TODO fixer ce bug afin de conserver l'identifiant et la date des opérations lors de la reconstruction
        //assertThat(domain.getHistory().get(0).id()).isEqualTo("op1");
        //assertThat(domain.getHistory().get(0).timestamp()).isEqualTo(LocalDateTime.parse("2025-04-10T14:30"));
        assertThat(domain.getHistory().get(0).type()).isEqualTo(BankOperation.OperationType.DEPOSIT);
        assertThat(domain.getHistory().get(0).amount().amount()).isEqualByComparingTo("100.00");
    }

    @Test
    void should_map_to_domain_without_operations_and_compute_balance() {
        // Given
        BankAccountEntity entity = new BankAccountEntity();
        entity.setId("ACC002");
        entity.setAuthorizedOverdraft(new BigDecimal("25.00"));
        LocalDateTime timestamp = LocalDateTime.of(2025, 4, 10, 14, 30);
        entity.setOperations(List.of(
                new BankOperationEntity("op1", "DEPOSIT", new BigDecimal("100.00"), timestamp, entity),
                new BankOperationEntity("op2", "WITHDRAWAL", new BigDecimal("30.00"), timestamp, entity)
        ));

        // When
        BankAccount domain = mapper.toDomainWithBalanceOnly(entity, operationFactory);

        // Then
        assertThat(domain.getId()).isEqualTo("ACC002");
        assertThat(domain.getBalance()).isEqualByComparingTo("70.00");
        assertThat(domain.getAuthorizedOverdraft().amount()).isEqualByComparingTo("25.00");
        assertThat(domain.getHistory()).isEmpty();
    }
}
