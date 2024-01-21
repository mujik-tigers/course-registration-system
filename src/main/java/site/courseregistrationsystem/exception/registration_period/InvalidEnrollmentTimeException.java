package site.courseregistrationsystem.exception.registration_period;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class InvalidEnrollmentTimeException extends CustomException {

	public InvalidEnrollmentTimeException() {
		super(ErrorType.ENROLLMENT_REGISTRATION_PERIOD_INVALID_TIME);
	}

}
