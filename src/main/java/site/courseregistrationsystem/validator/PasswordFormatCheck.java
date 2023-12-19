package site.courseregistrationsystem.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = PasswordFormatCheck.PasswordFormatValidator.class)
public @interface PasswordFormatCheck {

	String message() default "비밀번호는 8~16자로 영문, 숫자, 특수문자를 모두 사용해야 합니다";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	class PasswordFormatValidator implements ConstraintValidator<PasswordFormatCheck, String> {
		private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,16}$";

		private final Pattern pattern = Pattern.compile(PASSWORD_REGEX);

		@Override
		public boolean isValid(String studentId, ConstraintValidatorContext context) {
			return studentId != null && pattern.matcher(studentId).matches();
		}
	}

}
