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
@Constraint(validatedBy = StudentIdFormatCheck.StudentIdFormatValidator.class)
public @interface StudentIdFormatCheck {

	String message() default "학번은 9자리의 숫자입니다";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	class StudentIdFormatValidator implements ConstraintValidator<StudentIdFormatCheck, String> {
		private static final String STUDENT_ID_REGEX = "^[0-9]{9}$";

		private final Pattern pattern = Pattern.compile(STUDENT_ID_REGEX);

		@Override
		public boolean isValid(String studentId, ConstraintValidatorContext context) {
			return studentId != null && pattern.matcher(studentId).matches();
		}
	}

}
