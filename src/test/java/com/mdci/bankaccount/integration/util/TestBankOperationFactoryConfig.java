package com.mdci.bankaccount.integration.util;

import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.testutil.FakeBankOperationFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@TestConfiguration
public class TestBankOperationFactoryConfig {

    @Primary
    @Bean
    public BankOperationFactory fakeBankOperationFactory() {
        Clock fixed = Clock.fixed(Instant.parse("2025-03-01T00:00:00Z"), ZoneOffset.UTC);
        return new FakeBankOperationFactory(fixed);
    }
}
