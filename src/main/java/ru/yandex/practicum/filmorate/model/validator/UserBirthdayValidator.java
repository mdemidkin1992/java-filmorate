package ru.yandex.practicum.filmorate.model.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;


public class UserBirthdayValidator implements ConstraintValidator<BirthdayConstraint, LocalDate> {
    private static final LocalDate TODAY = LocalDate.now();

    @Override
    public boolean isValid(LocalDate birthday, ConstraintValidatorContext constraintValidatorContext) {
        return birthday.isBefore(TODAY);
    }
}

