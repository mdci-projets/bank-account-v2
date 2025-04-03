package com.mdci.bankaccount.domain.model;

import com.mdci.bankaccount.domain.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void shouldThrowExceptionIfNullAmountProvided() {
        assertThrows(InvalidAmountException.class, () -> new Money(null));
    }

    @Test
    void should_add_and_subtract_correctly() {
        Money m1 = Money.of(new BigDecimal("50.00"));
        Money m2 = Money.of(new BigDecimal("20.00"));

        assertThat(m1.add(m2)).isEqualTo(Money.of(new BigDecimal("70.00")));
        assertThat(m1.subtract(m2)).isEqualTo(Money.of(new BigDecimal("30.00")));
    }

    @Test
    void should_compare_properly() {
        assertThat(Money.of(new BigDecimal("50.00")))
                .isGreaterThan(Money.of(new BigDecimal("20.00")));
    }

    @Test
    void should_handle_negative_values() {
        Money negative = Money.of(new BigDecimal("-10.00"));
        assertThat(negative.isNegative()).isTrue();
    }
}


