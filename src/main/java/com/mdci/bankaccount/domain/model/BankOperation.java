package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

