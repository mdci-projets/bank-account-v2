package com.mdci.bankaccount.application.mapper;

import com.mdci.bankaccount.application.dto.AccountStatementDTO;
import com.mdci.bankaccount.application.dto.OperationDTO;
import com.mdci.bankaccount.domain.model.AccountStatement;
import com.mdci.bankaccount.domain.model.BankOperation;

import java.util.Comparator;
import java.util.List;

public class StatementMapper {

    public AccountStatementDTO toDto(AccountStatement statement) {
        List<OperationDTO> operations = statement.operations().stream()
                .sorted(Comparator.comparing(BankOperation::timestamp).reversed())
                .map(op -> new OperationDTO(
                        op.id(),
                        op.type().name(),
                        op.amount().amount(),
                        op.timestamp()
                ))
                .toList();

        return new AccountStatementDTO(
                statement.accountId(),
                statement.accountType(),
                statement.currentBalance(),
                statement.issuedAt(),
                statement.from(),
                statement.to(),
                operations
        );
    }
}

