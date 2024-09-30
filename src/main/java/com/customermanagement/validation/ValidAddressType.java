package com.customermanagement.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAddressTypeValidator.class)
@Documented
public @interface ValidAddressType {
    String message() default "Invalid address type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}