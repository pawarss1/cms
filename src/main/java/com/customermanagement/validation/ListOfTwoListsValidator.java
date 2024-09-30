package com.customermanagement.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ListOfTwoListsValidator implements ConstraintValidator<ListOfTwoLists, List<List<String>>> {

    @Override
    public boolean isValid(List<List<String>> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.size() == 2 && value.get(0) != null && value.get(1) != null;
    }
}