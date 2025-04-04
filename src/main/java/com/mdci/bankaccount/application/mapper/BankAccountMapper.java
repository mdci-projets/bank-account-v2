package com.mdci.bankaccount.application.mapper;

import com.mdci.bankaccount.domain.model.AccountType;
import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.application.dto.BankAccountResponseDTO;
import com.mdci.bankaccount.domain.model.Money;
import com.mdci.bankaccount.domain.model.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    BankAccountMapper INSTANCE = Mappers.getMapper(BankAccountMapper.class);

    @Mapping(target = "authorizedOverdraft", source = "authorizedOverdraft")
    @Mapping(target = "depositCeiling", expression = "java(getDepositCeiling(account))")
    BankAccountResponseDTO toResponseDTO(BankAccount account);

    default BigDecimal map(Money money) {
        return money != null ? money.amount() : null;
    }

    default BigDecimal getDepositCeiling(BankAccount account) {
        if (account instanceof SavingsAccount savings) {
            return savings.getDepositCeiling().amount();
        }
        return null;
    }

    default BigDecimal getAuthorizedOverdraft(BankAccount account) {
        if (account.getAccountType() == AccountType.LIVRET) return null;
        return account.getAuthorizedOverdraft().amount();
    }
}
