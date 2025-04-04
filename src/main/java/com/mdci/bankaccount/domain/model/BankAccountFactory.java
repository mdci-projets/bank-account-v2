package com.mdci.bankaccount.domain.model;

import java.math.BigDecimal;
import java.util.List;

public class BankAccountFactory {
    public final static Money DEPOSIT_CEILING = Money.of(BigDecimal.valueOf(22950));

    /**
     * Crée un nouveau compte bancaire (courant ou livret) avec dépôt initial.
     */
    public static BankAccount create(String id,
                                     Money initialBalance,
                                     Money authorizedOverdraft,
                                     BankOperationFactory operationFactory,
                                     AccountType accountType,
                                     Money depositCeiling) {
        if (accountType == null) {
            throw new IllegalArgumentException("Le type de compte est requis pour recharger un compte.");
        }
        BankAccount account = switch (accountType) {
            case COMPTE_COURANT -> new BankAccount(id, operationFactory, Money.zero(), authorizedOverdraft, accountType);
            case LIVRET -> new SavingsAccount(id, operationFactory, Money.zero(), depositCeiling);
        };

        account.deposit(initialBalance);
        return account;
    }

    /**
     * Reconstitue un compte bancaire depuis la base de données, avec historique complet.
     */
    public static BankAccount rehydrate(String id,
                                        BankOperationFactory operationFactory,
                                        Money authorizedOverdraft,
                                        List<BankOperation> operations,
                                        AccountType accountType,
                                        Money depositCeiling) {

        if (accountType == null) {
            throw new IllegalArgumentException("Le type de compte est requis pour recharger un compte.");
        }
        BankAccount account = switch (accountType) {
            case COMPTE_COURANT -> new BankAccount(id, Money.zero(), authorizedOverdraft, operationFactory, accountType);
            case LIVRET -> new SavingsAccount(id, operationFactory, Money.zero(), depositCeiling);
        };

        account.loadOperations(operations);
        return account;
    }

    /**
     * Reconstitue un compte bancaire depuis la base de données, avec seulement la balance.
     */
    public static BankAccount rehydrateWithBalanceOnly(String id,
                                                       BankOperationFactory operationFactory,
                                                       Money balance,
                                                       Money authorizedOverdraft,
                                                       AccountType accountType,
                                                       Money depositCeiling) {

        if (accountType == null) {
            throw new IllegalArgumentException("Le type de compte est requis pour recharger un compte.");
        }
        return switch (accountType) {
            case COMPTE_COURANT -> new BankAccount(id, balance, authorizedOverdraft, operationFactory, accountType);
            case LIVRET -> new SavingsAccount(id, operationFactory, balance, depositCeiling);
        };
    }

    /**
     * Calcule le solde à partir des opérations (utile pour reconstitution).
     */
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
