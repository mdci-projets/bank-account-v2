package com.mdci.bankaccount.domain.port.out;

import com.mdci.bankaccount.domain.model.BankOperation;

import java.time.LocalDate;
import java.util.List;

public interface IBankOperationRepository {
    BankOperation save(BankOperation operation);
    List<BankOperation> findAllByAccountId(String accountId);
    List<BankOperation> findAllByAccountIdUntilDate(String accountId, LocalDate date);
}
