package site.courseregistrationsystem.exception.enrollment;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class EnrollmentLimitExceededException extends CustomException {

	public EnrollmentLimitExceededException() {
		super(ErrorType.SEMESTER_CREDIT_EXCEED);
	}

}
