package com.mdci.bankaccount.domain.model;

import java.math.BigDecimal;
import java.util.List;

public class BankAccountFactory {

    /**
     * Crée un nouveau compte bancaire avec dépôt initial.
     */
    public static BankAccount create(String id, Money initialBalance, Money authorizedOverdraft, BankOperationFactory operationFactory) {
        BankAccount account = new BankAccount(id, operationFactory, Money.zero(), authorizedOverdraft);
        account.deposit(initialBalance);
        return account;
    }

    /**
     * Reconstitue un compte bancaire depuis la base de données, avec historique complet.
     */
    public static BankAccount rehydrate(String id, BankOperationFactory operationFactory, Money authorizedOverdraft, List<BankOperation> operations) {
        BankAccount account = new BankAccount(id, Money.zero(), authorizedOverdraft, operationFactory);
        account.loadOperations(operations);
        return account;
    }

    /**
     * Reconstitue un compte bancaire depuis la base de données, avec seulement la balance.
     */
    public static BankAccount rehydrateWithBalanceOnly(String id, BankOperationFactory operationFactory, Money balance, Money authorizedOverdraft) {
        BankAccount account = new BankAccount(id, balance, authorizedOverdraft, operationFactory);
        return account;
    }

    public static BigDecimal computeBalanceFromOperations(List<BankOperation> operations) {
        BigDecimal balance = BigDecimal.ZERO;
        for (BankOperation op : operations) {
            switch (op.type()) {
                case DEPOSIT -> balance = balance.add(op.amount().amount());
                case WITHDRAWAL -> balance = balance.subtract(op.amount().amount());
            }
        }
        return balance;
    }
}
