package com.curso.jogo2026.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CnpjValidator.class)
public @interface CnpjValido {
    String message() default "CNPJ inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
