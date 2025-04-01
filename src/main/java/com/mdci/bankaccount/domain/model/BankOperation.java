package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public record BankOperation(String id, OperationType type, Money amount, LocalDateTime timestamp) {

    public BankOperation {
        if (amount == null || amount.amount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Le montant doit être positif et non nul.");
        }
        if (id == null || id.isBlank()) {
            throw new InvalidAmountException("L'identifiant de l'opération est requis.");
        }
        if (timestamp == null) {
            throw new InvalidAmountException("La date de l'opération est requise.");
        }
    }

    public static BankOperation deposit(Money amount, Clock clock) {
        return new BankOperation(
                UUID.randomUUID().toString(),
                OperationType.DEPOSIT,
                amount,
                LocalDateTime.now(clock)
        );
    }

    public static BankOperation withdrawal(Money amount, Clock clock) {
        return new BankOperation(
                UUID.randomUUID().toString(),
                OperationType.WITHDRAWAL,
                amount,
                LocalDateTime.now(clock)
        );
    }

    public BigDecimal value() {
        return switch (type) {
            case DEPOSIT -> amount.amount();
            case WITHDRAWAL -> amount.amount().negate();
        };
    }

    public enum OperationType {
        DEPOSIT, WITHDRAWAL
    }
}

