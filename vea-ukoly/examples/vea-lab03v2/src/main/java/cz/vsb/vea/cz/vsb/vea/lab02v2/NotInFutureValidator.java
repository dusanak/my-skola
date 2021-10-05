package cz.vsb.vea.cz.vsb.vea.lab02v2;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotInFutureValidator implements ConstraintValidator<NotInFuture, LocalDate>{

	@Override
	public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
		return LocalDate.now().isAfter(date);
	}

}
