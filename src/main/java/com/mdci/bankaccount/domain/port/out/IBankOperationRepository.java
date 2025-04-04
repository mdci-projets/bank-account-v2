package com.mdci.bankaccount.domain.port.out;

import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.domain.model.BankOperation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IBankOperationRepository {
    BankOperation save(BankAccount account, BankOperation operation);
    List<BankOperation> findAllByAccountId(String accountId);
    List<BankOperation> findAllByAccountIdUntilDate(String accountId, LocalDate date);
    List<BankOperation> findAllByAccountIdBetweenDates(String accountId, LocalDateTime from, LocalDateTime to);
}
