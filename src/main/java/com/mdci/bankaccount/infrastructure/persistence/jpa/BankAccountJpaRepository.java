package com.mdci.bankaccount.infrastructure.persistence.jpa;

import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BankAccountJpaRepository extends JpaRepository<BankAccountEntity, String> {
    @Query("SELECT a FROM BankAccountEntity a LEFT JOIN FETCH a.operations WHERE a.id = :id")
    Optional<BankAccountEntity> findByIdWithOperations(@Param("id") String id);
}
