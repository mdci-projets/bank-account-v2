package com.mdci.bankaccount.application.validation;

import com.mdci.bankaccount.application.dto.CreateAccountRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateAccountValidatorTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_fail_when_livret_has_authorized_overdraft() {
        var dto = new CreateAccountRequestDTO(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                "LIVRET"
        );

        Set<jakarta.validation.ConstraintViolation<CreateAccountRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("découvert"));
    }


    @Test
    void should_pass_when_livret_has_valid_data() {
        var dto = new CreateAccountRequestDTO(
                BigDecimal.valueOf(500),
                BigDecimal.ZERO,
                "LIVRET"
        );

        Set violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}
