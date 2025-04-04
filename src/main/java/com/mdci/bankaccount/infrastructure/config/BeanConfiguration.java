package com.mdci.bankaccount.infrastructure.config;

import com.mdci.bankaccount.application.mapper.StatementMapper;
import com.mdci.bankaccount.application.service.BankAccountLoader;
import com.mdci.bankaccount.application.service.BankAccountService;
import com.mdci.bankaccount.application.service.BankAccountStatementService;
import com.mdci.bankaccount.application.service.BankOperationService;
import com.mdci.bankaccount.domain.port.out.BankOperationFactory;
import com.mdci.bankaccount.domain.port.in.IBankAccountService;
import com.mdci.bankaccount.domain.port.in.IBankAccountStatementService;
import com.mdci.bankaccount.domain.port.in.IBankOperationService;
import com.mdci.bankaccount.domain.port.out.IBankAccountRepository;
import com.mdci.bankaccount.domain.port.out.IBankOperationRepository;
import com.mdci.bankaccount.infrastructure.operation.DefaultBankOperationFactory;
import com.mdci.bankaccount.infrastructure.persistence.adapter.BankAccountRepositoryAdapter;
import com.mdci.bankaccount.infrastructure.persistence.adapter.BankOperationRepositoryAdapter;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankAccountJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.jpa.BankOperationJpaRepository;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankAccountEntityMapper;
import com.mdci.bankaccount.infrastructure.persistence.mapper.BankOperationEntityMapper;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
        return new DefaultBankOperationFactory(clock);
    }

    @Bean
    public BankAccountLoader accountLoader(IBankAccountRepository accountRepository,
                                           IBankOperationRepository operationRepository) {
        return new BankAccountLoader(accountRepository, operationRepository);
    }

    @Bean
    public IBankAccountService bankAccountService(IBankOperationRepository operationRepository,
                                                  IBankAccountRepository repository,
                                                  BankOperationFactory operationFactory,
                                                  Clock clock,
                                                  BankAccountLoader accountLoader) {
        return new BankAccountService(operationRepository, repository, operationFactory, clock, accountLoader);
    }

    @Bean
    public IBankOperationService bankOperationService(BankAccountLoader accountLoader,
                                                      IBankAccountRepository accountRepository,
                                                      IBankOperationRepository operationRepository) {
        return new BankOperationService(accountLoader, accountRepository, operationRepository);
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

    @Bean
    public StatementMapper statementMapper() {
        return new StatementMapper();
    }

    @Bean
    public IBankAccountStatementService bankAccountStatementServicee(IBankAccountRepository accountRepository, IBankOperationRepository operationRepository) {
        return new BankAccountStatementService(accountRepository, operationRepository);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestion Bancaire")
                        .version("1.0")
                        .description("Documentation de l’API pour la gestion des opérations bancaires")
                        .contact(new Contact()
                                .name("Support Développement")
                                .email("support@bankapi.com"))
                        .license(new License()
                                .name("Licence Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentation complète")
                        .url("https://bankapi.com/docs"));
    }
}

