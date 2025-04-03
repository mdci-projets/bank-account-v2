package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InvalidAmountException;

import java.math.BigDecimal;

public class Money implements Comparable<Money> {

    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("Le montant doit être positif et non nul.");
        }
        this.amount = amount;
        //this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    public Money negate() {
        return new Money(this.amount.negate());
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal amount() {
        return amount;
    }

    @Override
    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amount.compareTo(money.amount) == 0; // ignore scale
    }

    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode(); // ignore scale
    }

    @Override
    public String toString() {
        return amount + " €";
    }
}



