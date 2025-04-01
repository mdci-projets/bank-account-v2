package com.mdci.bankaccount.infrastructure.config;

import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.port.in.IBankAccountService;
import com.mdci.bankaccount.application.service.BankAccountService;
import com.mdci.bankaccount.application.service.BankOperationService;
import com.mdci.bankaccount.domain.port.in.IBankOperationService;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BeanConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public BankOperationFactory bankOperationFactory(Clock clock) {
        return new BankOperationFactory(clock);
    }

    @Bean
    public IBankAccountService bankAccountService(IBankAccountRepository repository,
                                                  BankOperationFactory operationFactory,
                                                  Clock clock) {
        return new BankAccountService(repository, operationFactory, clock);
    }

    @Bean
    public IBankOperationService bankOperationService(IBankAccountRepository accountRepository,
                                                      IBankOperationRepository operationRepository) {
        return new BankOperationService(accountRepository, operationRepository);
    }
}

