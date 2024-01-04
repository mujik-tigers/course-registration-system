package site.courseregistrationsystem.exception.enrollment;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class EnrollmentNotFoundException extends CustomException {

	public EnrollmentNotFoundException() {
		super(ErrorType.NONEXISTENT_ENROLLMENT);
	}

}
