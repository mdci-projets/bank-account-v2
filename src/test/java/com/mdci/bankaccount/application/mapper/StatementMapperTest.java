package com.mdci.bankaccount.application.mapper;

import com.mdci.bankaccount.application.dto.AccountStatementDTO;
import com.mdci.bankaccount.domain.model.AccountStatement;
import com.mdci.bankaccount.domain.model.AccountType;
import com.mdci.bankaccount.domain.model.BankOperation;
import com.mdci.bankaccount.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StatementMapperTest {

    private final StatementMapper mapper = new StatementMapper();

    @Test
    void should_map_statement_to_dto() {
        var op1 = new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.DEPOSIT, Money.of(BigDecimal.valueOf(100)), LocalDateTime.now().minusDays(2));
        var op2 = new BankOperation(UUID.randomUUID().toString(), BankOperation.OperationType.WITHDRAWAL, Money.of(BigDecimal.valueOf(50)), LocalDateTime.now().minusDays(1));

        var statement = new AccountStatement(
                "ACC123",
                AccountType.COMPTE_COURANT,
                new BigDecimal("1050"),
                LocalDateTime.now(),
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now(),
                List.of(op1, op2)
        );

        AccountStatementDTO dto = mapper.toDto(statement);

        assertThat(dto.accountId()).isEqualTo("ACC123");
        assertThat(dto.recentOperations()).hasSize(2);
        assertThat(dto.recentOperations().get(0).date()).isAfter(dto.recentOperations().get(1).date());
    }
}
