package com.mdci.bankaccount.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OperationDTO(
        String operationId,
        String type,
        BigDecimal amount,
        LocalDateTime date
) {
}
