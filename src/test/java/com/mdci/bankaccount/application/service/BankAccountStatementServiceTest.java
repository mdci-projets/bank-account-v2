package com.mdci.bankaccount.application.service;

import com.mdci.bankaccount.domain.exception.AccountNotFoundException;
import com.mdci.bankaccount.domain.exception.FunctionalException;
import com.mdci.bankaccount.domain.model.*;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import com.mdci.bankaccount.testutil.FakeBankOperationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BankAccountStatementServiceTest {

    private IBankAccountRepository accountRepository;
    private IBankOperationRepository operationRepository;
    private BankAccountStatementService service;

    @BeforeEach
    void setup() {
        accountRepository = mock(IBankAccountRepository.class);
        operationRepository = mock(IBankOperationRepository.class);
        service = new BankAccountStatementService(accountRepository, operationRepository);
    }

    @Test
    void should_generate_account_statement_for_given_period() {
        // Given
        String accountId = "ACC-001";
        Clock fixedClock = Clock.fixed(
                LocalDateTime.of(2025, 3, 1, 12, 0).toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
        );
        BankOperationFactory factory = new FakeBankOperationFactory(fixedClock);

        BankAccount account = new BankAccount(
                accountId,
                factory,
                Money.of(BigDecimal.valueOf(1000)),
                Money.of(BigDecimal.valueOf(200)),
                AccountType.COMPTE_COURANT
        );

        LocalDateTime from = LocalDateTime.now().minusDays(30);
        LocalDateTime to = LocalDateTime.now();

        List<BankOperation> ops = List.of(
                new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.DEPOSIT, Money.of(BigDecimal.valueOf(300)), to.minusDays(10)),
                new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.WITHDRAWAL, Money.of(BigDecimal.valueOf(150)), to.minusDays(5))
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(operationRepository.findAllByAccountIdBetweenDates(accountId, from, to)).thenReturn(ops);

        // When
        AccountStatement statement = service.generateStatementForPeriod(accountId, from, to);

        // Then
        assertThat(statement.accountId()).isEqualTo(accountId);
        assertThat(statement.accountType()).isEqualTo(AccountType.COMPTE_COURANT);
        assertThat(statement.currentBalance()).isEqualByComparingTo(account.getBalance());
        assertThat(statement.operations()).hasSize(2);
        assertThat(statement.issuedAt()).isEqualTo(to);
        assertThat(statement.from()).isEqualTo(from);
        assertThat(statement.to()).isEqualTo(to);
    }

    @Test
    void should_throw_if_account_not_found() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generateStatementForPeriod("unknown", null, null))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Aucun compte trouvé");
    }

    @Test
    void should_throw_if_from_is_after_to() {
        String accountId = "ACC-002";
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(mock(BankAccount.class)));

        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.minusDays(1);

        assertThatThrownBy(() -> service.generateStatementForPeriod(accountId, from, to))
                .isInstanceOf(FunctionalException.class)
                .hasMessageContaining("La date de début doit être antérieure");
    }
}
