package com.mdci.bankaccount.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mdci.bankaccount.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setUp() {
        account = new BankAccount();
    }

    @Test
    void shouldStartWithZeroBalance() {
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void shouldDepositMoney() {
        account.deposit(new Money(BigDecimal.valueOf(200)));
        assertEquals(BigDecimal.valueOf(200), account.getBalance());
    }

    @Test
    void shouldWithdrawMoney() {
        account.deposit(new Money(BigDecimal.valueOf(100)));
        account.withdraw(new Money(BigDecimal.valueOf(30)));
        assertEquals(BigDecimal.valueOf(70), account.getBalance());
    }

    @Test
    void shouldThrowWhenWithdrawingTooMuch() {
        account.deposit(new Money(BigDecimal.valueOf(50)));
        assertThrows(InsufficientBalanceException.class, () ->
                account.withdraw(new Money(BigDecimal.valueOf(100)))
        );
    }

    @Test
    void shouldGenerateUniqueAccountId() {
        assertNotNull(account.getId());
    }
}

