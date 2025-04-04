package com.mdci.bankaccount.application.validation;

import com.mdci.bankaccount.application.dto.CreateAccountRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class CreateAccountValidator implements ConstraintValidator<ValidAccountCreation, CreateAccountRequestDTO> {

    @Override
    public boolean isValid(CreateAccountRequestDTO dto, ConstraintValidatorContext context) {
        boolean isValid = true;

        // Règle : Livret → pas de découvert autorisé
        if ("LIVRET".equals(dto.accountType()) && dto.authorizedOverdraft().compareTo(BigDecimal.ZERO) > 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{bank.account.livret.overdraft}")
                    .addPropertyNode("authorizedOverdraft")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}

