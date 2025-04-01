package com.mdci.bankaccount.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;
import com.mdci.bankaccount.domain.exception.InvalidAmountException;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithPositiveAmount() {
        Money money = new Money(BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(100), money.amount());
    }

    @Test
    void shouldCreateMoneyWithZeroAmount() {
        Money money = new Money(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, money.amount());
    }

    @Test
    void shouldThrowExceptionForNegativeAmount() {
        assertThrows(InvalidAmountException.class, () ->
                new Money(BigDecimal.valueOf(-10))
        );
    }

    @Test
    void shouldAddTwoMoneyObjectsCorrectly() {
        Money m1 = new Money(BigDecimal.valueOf(50));
        Money m2 = new Money(BigDecimal.valueOf(30));
        Money result = m1.add(m2);
        assertEquals(BigDecimal.valueOf(80), result.amount());
    }

    @Test
    void shouldSubtractMoneyCorrectly() {
        Money m1 = new Money(BigDecimal.valueOf(100));
        Money m2 = new Money(BigDecimal.valueOf(40));
        Money result = m1.subtract(m2);
        assertEquals(BigDecimal.valueOf(60), result.amount());
    }

    @Test
    void shouldThrowExceptionWhenSubtractingTooMuch() {
        Money m1 = new Money(BigDecimal.valueOf(20));
        Money m2 = new Money(BigDecimal.valueOf(50));
        assertThrows(InsufficientBalanceException.class, () -> m1.subtract(m2));
    }

    @Test
    void shouldThrowExceptionIfNullAmountProvided() {
        assertThrows(InvalidAmountException.class, () -> new Money(null));
    }
}


