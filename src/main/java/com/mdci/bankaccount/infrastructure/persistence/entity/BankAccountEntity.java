package com.mdci.bankaccount.infrastructure.persistence.entity;

import com.mdci.bankaccount.domain.model.AccountType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bank_accounts")
public class BankAccountEntity {

    @Id
    private String id;

    private String currency;

    @Column(name = "authorized_overdraft", nullable = false)
    private BigDecimal authorizedOverdraft = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal depositCeiling;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<BankOperationEntity> operations = new ArrayList<>();

    public BankAccountEntity() {}

    public BankAccountEntity(String id, List<BankOperationEntity> operations, String currency) {
        this.id = id;
        this.operations = operations;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAuthorizedOverdraft() {
        return authorizedOverdraft;
    }

    public void setAuthorizedOverdraft(BigDecimal authorizedOverdraft) {
        this.authorizedOverdraft = authorizedOverdraft;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getDepositCeiling() {
        return depositCeiling;
    }

    public void setDepositCeiling(BigDecimal depositCeiling) {
        this.depositCeiling = depositCeiling;
    }

    public List<BankOperationEntity> getOperations() {
        return operations;
    }

    public void setOperations(List<BankOperationEntity> operations) {
        this.operations = operations;
    }
}
