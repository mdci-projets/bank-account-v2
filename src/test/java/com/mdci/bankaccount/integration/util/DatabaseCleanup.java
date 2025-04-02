package com.mdci.bankaccount.integration.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleanup {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void clear() {
        entityManager.createNativeQuery("DELETE FROM bank_operations").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM bank_accounts").executeUpdate();
    }
}
