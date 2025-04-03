package com.mdci.bankaccount.infrastructure.rest.exception;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        String error,
        String message,
        String path,
        int status,
        LocalDateTime timestamp
) {}

