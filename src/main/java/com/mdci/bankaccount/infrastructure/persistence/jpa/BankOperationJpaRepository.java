package com.mdci.bankaccount.infrastructure.persistence.jpa;

import com.mdci.bankaccount.infrastructure.persistence.entity.BankOperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BankOperationJpaRepository extends JpaRepository<BankOperationEntity, String> {
    @Query("SELECT o FROM BankOperationEntity o WHERE o.account.id = :accountId AND o.timestamp <= :date")
    List<BankOperationEntity> findAllByAccountIdUntilDate(String accountId, LocalDateTime date);

    @Query("SELECT o FROM BankOperationEntity o WHERE o.account.id = :accountId ORDER BY o.timestamp")
    List<BankOperationEntity> findAllByAccountId(String accountId);
}
