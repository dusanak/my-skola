package cz.vsb.vea.cz.vsb.vea.lab02v2;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = NotInFutureValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotInFuture {
	String message() default "{notinfuture}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
