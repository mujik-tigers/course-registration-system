package site.courseregistrationsystem.exception.enrollment;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class DuplicateEnrollmentException extends CustomException {

	public DuplicateEnrollmentException() {
		super(ErrorType.ENROLLMENT_DUPLICATION);
	}

}
