package com.mdci.bankaccount.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreateAccountValidator.class)
@Documented
public @interface ValidAccountCreation {
    String message() default "Les données du compte sont incohérentes.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
