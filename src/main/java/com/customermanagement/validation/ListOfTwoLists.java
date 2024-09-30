package com.customermanagement.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListOfTwoListsValidator.class)
@Documented
public @interface ListOfTwoLists {
    String message() default "List must contain exactly two sublists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}