package com.mdci.bankaccount.application.mapper;

import com.mdci.bankaccount.domain.model.BankAccount;
import com.mdci.bankaccount.application.dto.BankAccountResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    BankAccountMapper INSTANCE = Mappers.getMapper(BankAccountMapper.class);

    BankAccountResponseDTO toResponseDTO(BankAccount account);
}
