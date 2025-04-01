package com.mdci.bankaccount.infrastructure.persistence.jpa;

import com.mdci.bankaccount.infrastructure.persistence.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountJpaRepository extends JpaRepository<BankAccountEntity, String> {
}
