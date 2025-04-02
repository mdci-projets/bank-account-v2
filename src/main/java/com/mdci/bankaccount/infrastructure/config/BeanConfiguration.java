package com.mdci.bankaccount.infrastructure.config;

import com.mdci.bankaccount.application.service.BankAccountService;
import com.mdci.bankaccount.application.service.BankOperationService;
import com.mdci.bankaccount.domain.model.BankOperationFactory;
import com.mdci.bankaccount.domain.port.in.IBankAccountService;
import com.mdci.bankaccount.domain.port.in.IBankOperationService;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import com.mdci.bankaccount.infrastructure.persistence.adapter.BankAccountRepositoryAdapter;
import com.mdci.bankaccount.infrastructure.persistence.adapter.BankOperationRepositoryAdapter;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankAccountJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankOperationJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankAccountEntityMapper;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankOperationEntityMapper;
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
    public IBankAccountService bankAccountService(IBankOperationRepository operationRepository,
                                                  IBankAccountRepository repository,
                                                  BankOperationFactory operationFactory,
                                                  Clock clock) {
        return new BankAccountService(operationRepository, repository, operationFactory, clock);
    }

    @Bean
    public IBankOperationService bankOperationService(IBankAccountRepository accountRepository,
                                                      IBankOperationRepository operationRepository) {
        return new BankOperationService(accountRepository, operationRepository);
    }

    @Bean
    public BankAccountEntityMapper bankAccountEntityMapper() {
        return new BankAccountEntityMapper();
    }

    @Bean
    public IBankAccountRepository bankAccountRepository(BankAccountJpaRepository jpa,
                                                        BankAccountEntityMapper mapper,
                                                        BankOperationFactory factory) {
        return new BankAccountRepositoryAdapter(jpa, mapper, factory);
    }

    @Bean
    public BankOperationEntityMapper bankOperationEntityMapper() {
        return new BankOperationEntityMapper();
    }

    @Bean
    public IBankOperationRepository bankOperationRepository(BankOperationJpaRepository opJpa,
                                                            BankAccountJpaRepository accJpa,
                                                            BankOperationEntityMapper mapper) {
        return new BankOperationRepositoryAdapter(opJpa, accJpa, mapper);
    }
}

