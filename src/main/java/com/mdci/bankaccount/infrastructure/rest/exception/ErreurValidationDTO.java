package com.mdci.bankaccount.infrastructure.rest.exception;

public record ErreurValidationDTO(
        String champ,
        String message
) {}
